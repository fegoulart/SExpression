import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;


class Result {

    /*
     * Complete the 'SExpression' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING nodes as parameter.
     */

    static class Node {
        Node left, right;
        String data;

        Node(String data) {
            this.data = data;
            left = right = null;
        }
    }

    static TreeMap<String, List<String>> adjencyMap = new TreeMap<>();
    static TreeMap<String, Integer> countNodes = new TreeMap<>();
    static HashMap<String, Node> treeNodes = new HashMap<>();
    static List<String> nodesLabel = new ArrayList<>();
    static TreeMap<String, Boolean> checkTraversedKeys = new TreeMap<>();

    //\(([a-zA-Z]),([a-zA-Z])\)+

    static int countTreeNodes = 0;

    static void inOrderTraversal(Node root) {
        if (root != null) {
            inOrderTraversal(root.left);
            countTreeNodes++;
            inOrderTraversal(root.right);
        }
    }


    static String traverseMap(String key) {

        checkTraversedKeys.put(key, true);

        String response = "(" + key;

        if (countNodes.get(key) == null) {
            countNodes.put(key, 1);
        } else {
            int countLetter = countNodes.get(key);
            if (countLetter == 0) {
                countNodes.put(key, 1);
            } else {
                countNodes.put(key, countLetter + 1);
            }
        }

        List<String> nextKeys = adjencyMap.get(key);
        if (nextKeys == null || nextKeys.size() == 0) {
            return response + ")";
        }

        for (int i = 0; i < nextKeys.size(); i++) {
            response = response + traverseMap(nextKeys.get(i));
        }

        return response += ")";

    }


    public static String SExpression(String nodes) {
        // Write your code here
        String error = "E5";
        Boolean hasError = false;


        Pattern pattern = Pattern.compile("\\(([a-zA-Z]),([a-zA-Z])\\)+");
        Matcher matcher = pattern.matcher(nodes);

        int count = 0;
        while (matcher.find()) {
            count++;

            String x = nodes.substring(matcher.start() + 1, matcher.start() + 2);
            String y = nodes.substring(matcher.end() - 2, matcher.end() - 1);

            if (x.compareTo(y) == 0) {
                hasError = true;
            } else {

                String key;
                String value;

                if (x.compareTo(y) < 0) {
                    key = x;
                    value = y;
                } else {
                    key = y;
                    value = x;
                }


                List<String> myList = adjencyMap.get(key);
                if (myList == null) {
                    myList = new ArrayList<>();
                }

                boolean isRepeated = false;
                for (int i=0; i<myList.size();i++) {
                    if (value.equals(myList.get(i))) {
                        isRepeated = true;
                        hasError = false;
                        if (error != "E1") {
                            error = "E2";
                        }
                    }
                }

                if (isRepeated == false ) {
                    myList.add(value);
                }
                adjencyMap.put(key, myList);

                nodesLabel.add(key);
                nodesLabel.add(value);

                checkTraversedKeys.put(key, false);
            }
        }

        if (count == 0) {
            hasError = true;
        }

        String firstKey = "A";
        count = 0;

        Collections.sort(nodesLabel);

        String previousLabel = "";
        for (int i = 0; i < nodesLabel.size(); i++) {
            if (nodesLabel.get(i) != previousLabel) {
                treeNodes.put(nodesLabel.get(i), new Node(nodesLabel.get(i)));
            }
            previousLabel = nodesLabel.get(i);
        }

        Node myRoot = new Node("A");

        //Sort adjency list and creates a binary tree
        for (String key : adjencyMap.keySet()) {
            if (count == 0) {
                firstKey = key;
                myRoot = treeNodes.get(key);
            }
            Node parent = treeNodes.get(key);

            Collections.sort(adjencyMap.get(key));
            for (int i = 0; i < adjencyMap.get(key).size(); i++) {
                if (i == 0) {
                    parent.left = treeNodes.get(adjencyMap.get(key).get(i));
                } else {
                    if (i == 1) {
                        if (parent.left == treeNodes.get(adjencyMap.get(key).get(i))) {
                            hasError = true;
                            if (error != "E1") {
                                error = "E2";
                            }
                        } else {
                            parent.right = treeNodes.get(adjencyMap.get(key).get(i));
                        }
                    } else {
                        hasError = true;
                        error = "E1";

                    }
                }
            }

            count++;
        }

        count = 0;
        for (String key : adjencyMap.keySet()) {
            String previous = "";
            for (int i = 0; i < adjencyMap.get(key).size(); i++) {
                if (adjencyMap.get(key).get(i) == previous) {
                    hasError = true;
                    if (error != "E1") {
                        error = "E2";
                    }
                }

            }
            count++;
        }

        String myReturn = traverseMap(firstKey);

        //System.out.println(myReturn);

        for (String key : countNodes.keySet()) {
            if (countNodes.get(key) > 1) {
                hasError = true;
                if (error != "E1" && error != "E2") {
                    error = "E3";
                }
            }
        }

        for (String key : adjencyMap.keySet()) {
            if (adjencyMap.get(key).size() > 2) {
                hasError = true;
                error = "E1";
            }
        }


        //Check if multiple Roots
        countTreeNodes = 0;
        inOrderTraversal(myRoot);

        //Check if order is ok
        boolean lexicoOk = true;
        for (String key : checkTraversedKeys.keySet()) {
            if (checkTraversedKeys.get(key) == false) {
                lexicoOk = false;
                hasError = true;
            }
        }


        if (countTreeNodes < treeNodes.size()) {
            hasError = true;
            if (error != "E1" && error != "E2" && error != "E3") {
                error = "E4";
            }
        }

        if (hasError) {
            return error;
        }

        return myReturn;
    }

}

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String nodes = bufferedReader.readLine();

        String result = Result.SExpression(nodes);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}

