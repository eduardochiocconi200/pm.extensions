package com.servicenow.processmining.extensions.tm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoadTaskMiningDataSetFromExcel
{
    private String fileLocation = null;
    private ArrayList<String> windowNameCodes = null;
    private HashMap<String, ArrayList<Integer>> dailySequences = null;

    public LoadTaskMiningDataSetFromExcel(final String fileLocation)
    {
        this.fileLocation = fileLocation;
    }

    public String getFileLocation()
    {
        return this.fileLocation;
    }

    private ArrayList<String> getWindowNameCodes()
    {
        if (this.windowNameCodes == null) {
            this.windowNameCodes = new ArrayList<String>();
        }

        return this.windowNameCodes;
    }

    public HashMap<String, ArrayList<Integer>> getDailySequences()
    {
        if (this.dailySequences == null) {
            this.dailySequences = new HashMap<String, ArrayList<Integer>>();
        }

        return dailySequences;
    }

    public boolean loadAppName(final int minNumberOfClicks, double minTimeInApp)
    {
        return load(true, minNumberOfClicks, minTimeInApp);
    }

    public boolean loadWindowName(final int minNumberOfClicks, double minTimeInWindow)
    {
        return load(false, minNumberOfClicks, minTimeInWindow);
    }

    private boolean load(final boolean appAnalysis, final int minNumberOfClicks, double minTimeInAppOrWindow)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(getFileLocation()))) {
            String currentDate = null;
            int rowNumber = 0;
            String lastWindowName = null;
            List<String> data = null;
            while ((data = readLine(br)) != null) {
                rowNumber++;
                if (rowNumber == 1) {
                    continue;
                }

                int numberOfClicks = Integer.valueOf(data.get(8)).intValue();
                if (numberOfClicks < minNumberOfClicks) {
                    continue;
                }

                NumberFormat usFormat = NumberFormat.getInstance(Locale.US);
                Number number = usFormat.parse(data.get(9));
                double timeInAppOrWindow = number.doubleValue();
                if (timeInAppOrWindow < minTimeInAppOrWindow) {
                    continue;
                }

                String appOrWindowName = appAnalysis ? data.get(5) : data.get(6);
                if (appOrWindowName == null || (appOrWindowName != null && appOrWindowName.equals(""))) {
                    continue;
                }

                appOrWindowName = cleanUpAppOrWindowName(appAnalysis, appOrWindowName);

                if (lastWindowName != null && lastWindowName.equals(appOrWindowName)) {
                    continue;
                }

                int windowNameIndex = getWindowNameCodes().indexOf(appOrWindowName) + 1;

                if (windowNameIndex == 0) {
                    if (appOrWindowName.equals("~Lock~")) {
                        windowNameIndex = -1;
                    }
                    else {
                        windowNameIndex = getWindowNameCodes().size() + 1;
                        getWindowNameCodes().add(appOrWindowName);
                    }
                }

                if (currentDate == null) {
                    currentDate = data.get(10);
                }
                else if (!currentDate.equals(data.get(10))) {
                    currentDate = data.get(10);
                }

                if (currentDate != null) {
                    currentDate = currentDate.substring(0, currentDate.indexOf(" "));
                }

                if (getDailySequences().get(currentDate) == null) {
                    getDailySequences().put(currentDate, new ArrayList<Integer>());
                }

                getDailySequences().get(currentDate).add(windowNameIndex);
                lastWindowName = appOrWindowName;
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return false;
        } catch (ParseException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return false;
        }

        if (!checkNoConsecutiveDuplicatesInSequence()) {
            System.err.println("There is at least a sequence with two consecutive ids, which is not allowed");
            return false;
        }

        return true;
    }

    private String cleanUpAppOrWindowName(boolean appAnalysis, String appOrWindowName)
    {
        if (!appAnalysis) {
            // If the app is Google, we can clean up the window name.
            for (int i=0; i < windowNameSuffixesToClean.length; i++) {
                if (appOrWindowName.indexOf(windowNameSuffixesToClean[i]) > 0) {
                    appOrWindowName = appOrWindowName.substring(0, appOrWindowName.indexOf(windowNameSuffixesToClean[i]));
                }
            }
        }

        return appOrWindowName;
    }

    private String[] windowNameSuffixesToClean = {
        " - High memory usage",
        " - Google Chrome -",
    };

    private List<String> readLine(final BufferedReader br)
    {
        List<String> data = null;
        try {
            String line = br.readLine();
            if (line == null)
                return null;

            data = parseCsvLine(line);
            if (data == null) {
                do {
                    String previousLine = line;
                    line = br.readLine();
                    line = previousLine + line;
                    data = parseCsvLine(line);
                } while (data == null);
            }

        } catch (IOException e) {
            return null;
        }

        return data;
    }

    private static List<String> parseCsvLine(final String line)
    {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // Toggle inQuotes state
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim()); // Add the value and reset
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c); // Append character to current value
            }
        }
        if (inQuotes) {
            return null;
        }
        values.add(currentValue.toString().trim()); // Add the last value
        return values;
    }

    private boolean checkNoConsecutiveDuplicatesInSequence()
    {
        for (String date : getDailySequences().keySet()) {
            ArrayList<Integer> fullDaySequence = getDailySequences().get(date);
            Integer lastSeqId = null;
            for (Integer seqId : fullDaySequence) {
                if (lastSeqId != null && lastSeqId.equals(seqId)) {
                    if (!lastSeqId.equals(-1)) {
                        System.err.println("Found repeated sequence with Id: (" + seqId + "), lastSeq: (" + lastSeqId + ") on date: (" + date + ")");
                        System.err.println("SEQ: (" + fullDaySequence + ")");
                        return false;
                    }
                }
                lastSeqId = seqId;
            }
        }

        return true;
    }

    public void printWindowNameCodes()
    {
        int row = 1;
        for (String windowName : getWindowNameCodes()) {
            System.out.println("Window Name[" + row + "] = (" + windowName + ")");
            row++;
        }
    }

    public boolean saveSequences(final String sequenceFile)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sequenceFile))) {
            for (String date : getDailySequences().keySet()) {
                if (date.startsWith("20")) {
                    ArrayList<Integer> fullDaySequence = getDailySequences().get(date);
                    int start = 0;
                    int end = 0;
                    int subListSize = 10000;
                    int iterations = fullDaySequence.size() < subListSize ? 1 : ((fullDaySequence.size() / subListSize)+1);
                    for (int i=0; i < iterations; i++) {
                        end = ((start+subListSize) <= fullDaySequence.size()) ? start+subListSize : fullDaySequence.size();
                        if (getDailySequences().get(date).size() == 1 && getDailySequences().get(date).get(0).equals(-1)) {
                            continue;
                        }
                        String line = getDailySequences().get(date).subList(start, end).toString();
                        line = line.substring(1, line.length()-1);
                        writer.write(line.replaceAll(",", "")+ " -1 -2");
                        writer.newLine();
                        start += subListSize;
                    }
                }
            }
            System.out.println("Lines successfully written to " + sequenceFile);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    public boolean saveWindowNameCodes(final String codesFile)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(codesFile))) {
            int row = 1;
            for (String windowName : getWindowNameCodes()) {
                writer.write(row + " - " + windowName);
                writer.newLine();
                row++;
            }
            System.out.println("Lines successfully written to " + codesFile);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}