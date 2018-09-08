package Hclient;

import java.io.*;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Reader {

    public static final ArrayList<String> sysnames = new ArrayList<>();

    public static void main(String o, String d, String secure) throws Exception {

        try {
            ArrayList<String> ids = Client.namesToID(o, d);
            String a = ids.get(0);
            String b = ids.get(1);

                Client.getRoute(a, b, secure);
                Client.getSysData();
                sysnames.clear();


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

        } catch (NullPointerException e) {
            sysnames.clear();
            sysnames.add("Unknown System");
            sysnames.add("Error");
        }

    }

}
