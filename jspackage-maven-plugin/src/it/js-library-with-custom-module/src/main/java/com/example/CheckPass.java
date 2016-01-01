package com.example;

import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.rhino.Node;

public class CheckPass implements CompilerPass {

    @Override
    public void process(Node externs,
                        Node root) {
        System.out.println("---- check pass ----");
        System.out.println(root);
    }
}