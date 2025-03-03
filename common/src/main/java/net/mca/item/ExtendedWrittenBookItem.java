package net.mca.item;

import net.mca.client.book.Book;
import net.mca.client.book.pages.TextPage;
import net.mca.cobalt.network.NetworkHandler;
import net.mca.network.s2c.OpenGuiRequest;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtendedWrittenBookItem extends WrittenBookItem {
    private final Book book;

    public ExtendedWrittenBookItem(Settings settings, Book book) {
        super(settings);
        this.book = book;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (book.getBookAuthor() != null) {
            tooltip.add(book.getBookAuthor());
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (player instanceof ServerPlayerEntity) {
            NetworkHandler.sendToPlayer(new OpenGuiRequest(OpenGuiRequest.Type.BOOK), (ServerPlayerEntity)player);
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    public Book getBook(ItemStack item) {
        NbtCompound tag = item.getNbt();
        if (tag != null && tag.contains("pages")) {
            //seems like a vanilla book, let's make a copy of the book
            Book book = this.book.copy();

            //add our text pages
            NbtList pages = tag.getList("pages", NbtElement.STRING_TYPE);
            for (int i = 0; i < pages.size(); i++) {
                book.addPage(new TextPage(pages.getString(i)));
            }

            return book;
        } else {
            return book;
        }
    }
}
