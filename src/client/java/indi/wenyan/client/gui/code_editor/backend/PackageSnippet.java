package indi.wenyan.client.gui.code_editor.backend;

import indi.wenyan.client.gui.code_editor.widget.PackageSnippetWidget;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Data
@Accessors(fluent = true)
public class PackageSnippet {
    final ItemStack itemStack;
    final String name;
    final List<PackageSnippetWidget.Member> members;
    boolean fold = false;

    public PackageSnippet(ItemStack itemStack, String name, List<PackageSnippetWidget.Member> members) {
        this.itemStack = itemStack;
        this.name = name;
        this.members = members;
    }
}
