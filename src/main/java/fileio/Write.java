package fileio;

import utility.Utility;
import graph.GraphNode;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: class comments
public class Write {

    private Map<String, GraphNode> _algorithmResultMap;
    private BufferedReader _bufferedReader;
    private String _outputPath;
    private String _inputPath;
    private File _outputFile;

    //Regex constants for comparing validity of name and nodes format
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_OUTPUT_FILENAME = ".*?/ .dot";
    //private static final String RGX_UNTIL_WEIGHT = "([.*])];";

    public Write(String inputPath, String outputPath) {
        _outputPath = outputPath;
        _inputPath = inputPath;
    }

    /**
     * writeToPath - writes algorithm resultant node map to output file
     * @param algorithmResultMap - map of vertices with algorithm result fields
     */
    public void writeToPath(Map<String, GraphNode> algorithmResultMap) {
        _algorithmResultMap = algorithmResultMap; //assign to field

        //Build file given output path
        try {
            _outputFile = new File(_outputPath);
            _outputFile.createNewFile();
            _bufferedReader = new BufferedReader(new FileReader(_inputPath));
            buildFile();
        } catch (IOException e) {
            System.err.println("File could not be read: " + _inputPath);
            Utility.printUsage();
            System.exit(406);
        }
    }

    /**
     * buildFile - append map to output file using algorithm's output of nodes
     * @throws IOException
     */
    private void buildFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(_outputPath));

        // read
        try {
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean firstLine = true;

            while (currentLine!= null) {
                if (lineIsNode(currentLine) != null) {
                    //Append appropriate middle line(s) structure
                    GraphNode graphNode = _algorithmResultMap.get(lineIsNode(currentLine));

                    // append results to each line
                    String appendToLine = ",Start=" + graphNode.getStartTime() + ",Processor=" + graphNode.getProcessor() + "];\n";
                    StringBuilder sb =  new StringBuilder();
                    sb.append(currentLine);
                    sb.setLength(sb.length()-2);
                    sb.append(appendToLine);
                    writer.write(sb.toString());

                } else if (firstLine) {
                    //Append appropriate firstline structure
                    StringBuilder sb =  new StringBuilder();
                    sb.append("digraph \"");
                    sb.append(_outputFile.getName());
                    sb.setLength(sb.length() - 4);
                    sb.append("\" {\n");
                    writer.write(sb.toString());
                    firstLine = false;

                } else {
                    //Append new line
                    writer.write(currentLine.concat("\n"));
                }

                //increment to next line of appending from input
                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.close();
    }

    /**
     * lineIsNode - checks if the current reading line is a node
     * @return true if line being read is node, false if not
     */
    private String lineIsNode(String line) {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Matcher matcherNode = patternNode.matcher(line);

        //Return node number if the current line is a node
        if (matcherNode.matches()) {
            return matcherNode.group(1);
        } else {
            return null;
        }
    }
}
