package indi.wenyan.annotation_processor;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;

public class PackageProviderTaskListener implements TaskListener {
    private final Context context;
    private final Log logger;

    public PackageProviderTaskListener(BasicJavacTask task) {
        context = task.getContext();
        logger = Log.instance(context);
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
