package indi.wenyan.annotation_processor;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

public class PackageProviderTaskListener implements TaskListener {

    public PackageProviderTaskListener() {
    }

    @Override
    public void finished(TaskEvent e) {
        if (e.getKind() != TaskEvent.Kind.PARSE) return;
        ((JCTree.JCCompilationUnit) e.getCompilationUnit()).accept(new Replacer());
    }

    public static class Replacer extends TreeTranslator {
        public Replacer() {

        }
    }
}
