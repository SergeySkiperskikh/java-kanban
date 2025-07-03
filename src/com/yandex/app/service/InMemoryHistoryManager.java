package com.yandex.app.service;

import com.yandex.app.model.*;
import com.yandex.app.utility.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode((task.getId()));
        linkLast(task);
        nodes.put(task.getId(), last);

    }

    @Override
    public void remove(Integer id) {
        removeNode(id);

    }

    private void removeNode(Integer id) {
        Node node = nodes.remove(id);
        if (node == null) {
            return;
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            first = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            last = node.getPrev();
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.setNext(node);
        }
        last = node;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = first;

        while (current != null) {
            historyList.add(current.getValue());
            current = current.getNext();
        }

        return historyList;
    }

}
