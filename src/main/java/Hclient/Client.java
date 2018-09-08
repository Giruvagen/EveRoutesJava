package Hclient;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

class Client {

    public static void getRoute(String d, String o, String safeflag) {

        String dest = new String(d);
        String ori = new String(o);
        String endo = null;
        switch (safeflag) {
            case "Secure":
                endo = "https://esi.evetech.net/latest/route/" + ori + "/" + dest + "/?datasource=tranquility&flag=secure";
                break;
            case "Insecure":
                endo = "https://esi.evetech.net/latest/route/" + ori + "/" + dest + "/?datasource=tranquility&flag=insecure";
                break;
            case "Shortest":
                endo = "https://esi.evetech.net/latest/route/" + ori + "/" + dest;
                break;
        }

        HttpClient hclient = new HttpClient();
        GetMethod hget = new GetMethod(endo);
        hclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());

        try {

            int statusCode = hclient.executeMethod(hget);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + hget.getStatusLine());
            }

            byte[] responseBody = hget.getResponseBody();

            FileWriter output = new FileWriter("out.json");
            for (byte aResponseBody : responseBody) {
                output.write(aResponseBody);
            }
            output.close();

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            hget.releaseConnection();
        }


    }

    static void getSysData() throws IOException, ParseException {

        ArrayList<String> systemids = new ArrayList<>();

        org.json.simple.parser.JSONParser parser = new JSONParser();
        JSONArray ids = (JSONArray) parser.parse(new FileReader("out.json"));

        for (int i = 0; i < ids.size(); i++) {

            Long in = (Long) ids.get(i);
            int ins = in.intValue();
            String inst = Integer.toString(ins);
            systemids.add(String.valueOf(ins));
        }

        FileWriter output = new FileWriter("names.json");


        for (int i = 0; i < systemids.size(); i++) {

            String sys = systemids.get(i);


            String endo = "https://esi.evetech.net/latest/universe/systems/" + sys + "/";
            HttpClient hclient = new HttpClient();
            GetMethod hget = new GetMethod(endo);

            hclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());

            try {

                int statusCode = hclient.executeMethod(hget);

                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + hget.getStatusLine());
                }

                byte[] responseBody = hget.getResponseBody();

                for (byte aResponseBody : responseBody) {
                    output.write(aResponseBody);
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                hget.releaseConnection();
            }
            output.write("\n");

        }
        output.close();

    }

    static ArrayList<String> getNames(String o, String d) throws Exception {

        ArrayList<String> sysnames = new ArrayList<>();
        File in = new File("names.json");

        LineIterator it = FileUtils.lineIterator(in, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                FileWriter lines = new FileWriter("line.json");
                lines.write(line);
                lines.close();
                Object obj = new JSONParser().parse(new FileReader("line.json"));
                JSONObject json = (JSONObject) obj;
                String sys = (String) json.get("name");
                sysnames.add(sys);

            }
        } finally {
            it.close();
        }
        return sysnames;


    }

    static ArrayList<String> namesToID(String o, String d) throws Exception {

        String dest;
        String ori;

        dest = o;
        ori = d;
        String endo = "https://esi.evetech.net/latest/universe/ids/";
        HttpClient hclient = new HttpClient();
        PostMethod hpost = new PostMethod(endo);
        String payload = "[\"" + ori + "\",\"" + dest + "\"]";
        StringRequestEntity json = new StringRequestEntity(payload, "application/json", "utf-8");
        hpost.addRequestHeader("Content-type", "application/json");
        hpost.setRequestEntity(json);
        ArrayList<String> outer = new ArrayList<>();

        hclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());

        try {

            int statusCode = hclient.executeMethod(hpost);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + hpost.getStatusLine());
                outer.add("System Name");
                outer.add("Not Found");
            }

            byte[] responseBody = hpost.getResponseBody();

            FileWriter output = new FileWriter("idtonames.json");
            for (byte aResponseBody : responseBody) {
                output.write(aResponseBody);
            }
            output.close();

            Object getids = new JSONParser().parse(new FileReader("idtonames.json"));
            JSONObject jsontwo = (JSONObject) getids;
            JSONArray site = (JSONArray) jsontwo.get("systems");
            JSONObject one = (JSONObject) site.get(0);
            JSONObject two = (JSONObject) site.get(1);
            String oriid = one.get("id").toString();
            String destid = two.get("id").toString();
            outer.add(oriid);
            outer.add(destid);

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            hpost.releaseConnection();
        }
        return outer;

    }
}
