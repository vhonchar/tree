package com.tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TreeParser {

    public static final String EMPTY_CHILD = "#";
    public static final String NODE_NAMES_DELIMITER = ",";

    public Tree createTree(InputStreamReader is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(is);
        Map<String, Node> parsedRootNodes = new HashMap<>();
        Map<String, Node> parsedLeafs = new HashMap<>();

        String fileLine;

        while ((fileLine = bufferedReader.readLine()) != null) {
            parseSingleLine(fileLine, parsedRootNodes, parsedLeafs);
        }

        if (parsedRootNodes.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Node root = parsedRootNodes.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(node -> !node.hasParent())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Stream has several roots"));

        return new Tree(root);
    }

    private Node parseSingleLine(String textLine, Map<String, Node> parsedHeadNodes, Map<String, Node> parsedLeafs) {
        String[] nodeParts = textLine.split(NODE_NAMES_DELIMITER);
        if (nodeParts.length != 3) {
            throw new IllegalArgumentException("Line (" + textLine + ") is in wrong format");
        }

        Node headNode = createHeadNode(nodeParts, parsedHeadNodes, parsedLeafs);

        String leftNodeName = nodeParts[1];
        if (isValidNodeName(leftNodeName)) {
            Node leftLeaf = getLeaf(parsedHeadNodes, leftNodeName, parsedLeafs);
            headNode.setLeft(leftLeaf);
            leftLeaf.setParent(headNode);
        }

        String rightNodeName = nodeParts[2];
        if (isValidNodeName(rightNodeName)) {
            Node rightLeaf = getLeaf(parsedHeadNodes, rightNodeName, parsedLeafs);
            headNode.setRight(rightLeaf);
            rightLeaf.setParent(headNode);
        }

        return headNode;
    }

    private boolean isValidNodeName(String leftNodeName) {
        return !EMPTY_CHILD.equals(leftNodeName);
    }

    private Node createHeadNode(String[] nodeParts, Map<String, Node> parsedHeadNodes, Map<String, Node> parsedLeafs) {
        String headNodeName = nodeParts[0];
        if (parsedHeadNodes.containsKey(headNodeName)) {
            throw new IllegalArgumentException("Node " + headNodeName + " is duplicated");
        }

        Node headNode = parsedLeafs.containsKey(headNodeName)
                ? parsedLeafs.get(headNodeName)
                : new Node(headNodeName);

        parsedHeadNodes.put(headNode.getName(), headNode);
        return headNode;
    }

    private Node getLeaf(Map<String, Node> parsedHeadNodes, String nodeName, Map<String, Node> parsedLeafs) {
        Node leaf = parsedHeadNodes.containsKey(nodeName)
                ? parsedHeadNodes.get(nodeName)
                : new Node(nodeName);

        parsedLeafs.put(leaf.getName(), leaf);
        return leaf;
    }

}
