package indi.wenyan.annotation_processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

@AutoService(com.sun.source.util.Plugin.class)
public class PackageProviderPlugin implements Plugin {
    @Override
    public String getName() {
        return "PackageProviderPlugin";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new PackageProviderTaskListener());
    }
}
