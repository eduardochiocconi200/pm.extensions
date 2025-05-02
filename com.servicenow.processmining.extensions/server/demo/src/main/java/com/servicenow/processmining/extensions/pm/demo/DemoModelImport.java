package com.servicenow.processmining.extensions.pm.demo;

import java.io.File;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

public class DemoModelImport
{
    String args[] = null;
    String dataIdentifier = null;
    String dataFile = null;
    String user = null;
    String password = null;
    String instance = null;
    boolean argsParsed = false;

    public static void main(String args[])
    {
        DemoModelImport dmi = new DemoModelImport(args);
        dmi.run();
    }

    public DemoModelImport(final String args[])
    {
        this.args = args;
    }

    public void run()
    {
        if (!parseArgs()) {
            System.exit(-1);
        }

        DemoModelParser parser = new DemoModelParser(dataFile, dataIdentifier);
        if (!parser.parse()) {
            System.err.println("Could not parse demo data input XLS file: (" + dataFile + ")");
            System.exit(-2);
        }

        ServiceNowInstance snInstance = new ServiceNowInstance(instance, user, password);
        DemoModelCases cases = new DemoModelCases(parser.getModel(), snInstance);
        if (!cases.create()) {
            System.err.println("Could not properly created ALL cases for the different paths defined in input XLS file.");
            System.exit(-3);
        }
    }

    private boolean parseArgs()
    {
        if (args.length != 8) {
            System.err.println(usage());
            return false;
        }

        for (int i=1; i < args.length; i=i+2) {
            if (args[i-1].equals("-f")) {
                dataFile = args[i];
            }
            else if (args[i-1].equals("-i")) {
                instance = args[i];
            }
            else if (args[i-1].equals("-u")) {
                user = args[i];
            }
            else if (args[i-1].equals("-p")) {
                password = args[i];
            }
            else if (args[i-1].equals("-d")) {
                dataIdentifier = args[i];
            }
            else {
                System.err.println(usage());
                return false;
            }
        }

        return checkArgumentValues();
    }

    private String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid input parameters to run the demo import case creation application. Check usage details and try again.\n\n");
        sb.append("Usage: java -jar pm-demo-data-import.jar -f [XLS file] -i instance -u user -p password\n");
        sb.append(" -f [specified the file location, that contains the details to create case instances along with their specific path updates.]\n");
        sb.append(" -i [specifies the instance name where the cases will be created (ie: processminingec1demo.service-now.com). Include domain suffix (ie: instance.service-now.com).]\n");
        sb.append(" -u [specifies the user in the instance specified in the -i parameter, that will own the created cases. Specify a user with enough permissions.]\n");
        sb.append(" -p [specifies the password for the user specified in the -u parameter.]\n");
        sb.append(" -d [specifies an id that will be available in all generated records so it is possible to track imported data.]\n\n");
        sb.append("For example:\n");
        sb.append("# java -jar pm-demo-data-import.jar -f demo.xslx -i processminingec1demo.service-now.com -u admin -p password -d ITSM-Demo-Data\n");

        return sb.toString();
    }

    private boolean checkArgumentValues()
    {
        if (dataFile == null) {
            System.err.println("ERROR: The -f argument and value need to be specified and is missing. Check usage details below.");
            System.err.println(usage());
            return false;
        }
        else {
            File f = new File(dataFile);
            if (!f.exists()) {
                System.err.println("ERROR: The file [" + dataFile + "] cannot be found. Make sure the file exists in the specified location.\n");
                System.err.println(usage());
                return false;    
            }
        }

        if (instance == null) {
            System.err.println("ERROR: The -i argument and value need to be specified and is missing. Check usage details below.");
            System.err.println(usage());
            return false;
        }

        if (user == null) {
            System.err.println("ERROR The -u argument and value need to be specified and is missing. Check usage details below.");
            System.err.println(usage());
            return false;
        }

        if (password == null) {
            System.err.println("ERROR The -p argument and value need to be specified and is missing. Check usage details below.");
            System.err.println(usage());
            return false;
        }

        return true;
    }
}
