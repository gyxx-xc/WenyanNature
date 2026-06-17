package indi.wenyan.content.block.cloud_beacon;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Deprecated
@SuppressWarnings("ALL")
public class GlobalPackageManager {
    private static GlobalPackageManager INSTANCE = null;
    private Map<String, Set<Entry>> byStr = new HashMap<>();
    private Map<BlockPos, Set<Entry>> byBeacon = new HashMap<>();

    private GlobalPackageManager() {}

    public static GlobalPackageManager getInstance() {
        if (INSTANCE == null) INSTANCE = new GlobalPackageManager();
        return INSTANCE;
    }

    public void register(BlockPos beacon, BlockPos module, String packageName) {
        Entry entry = new Entry(beacon, packageName, module);
        Set<Entry> entries = byStr.computeIfAbsent(packageName, _ -> new HashSet<>());
        Set<Entry> entries2 = byBeacon.computeIfAbsent(beacon, _ -> new HashSet<>());
        entries.add(entry);
        entries2.add(entry);
    }

    public void unregister(BlockPos beacon) {
        Set<Entry> entries = byBeacon.remove(beacon);
        for (var e : entries) {
            var strEntries = byStr.get(e.packageName);
            strEntries.remove(e);
            if (strEntries.isEmpty()) {
                byStr.remove(e.packageName);
            }
        }
    }

    public @Nullable BlockPos getPackage(String packageName) {
        Set<Entry> entries = byStr.get(packageName);
        if (entries == null) return null;
        return entries.stream().findFirst().map(Entry::module).orElse(null);
    }

    public record Entry(BlockPos beacon, String packageName, BlockPos module){}
}
