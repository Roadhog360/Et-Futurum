package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockWoodSign extends BlockSign {

	protected static BlockWoodSign prevSign;
	private BlockWoodSign wallSign;
	private final BlockWoodSign standingSign;

	private final Block baseBlock;
	private final int meta;
	public final String type;

	public final boolean standing;

	public BlockWoodSign(Class<? extends TileEntity> p_i45426_1_, boolean p_i45426_2_, String type, Block block, int meta) {
		super(p_i45426_1_, p_i45426_2_);
		this.meta = meta;
		baseBlock = block;
		this.type = type;
		standing = p_i45426_2_;
		if (standing) {
			prevSign = this;
			standingSign = this;
		} else {
			standingSign = prevSign;
			wallSign = this;
			prevSign.wallSign = this;
		}
		setHardness(1.0F);
		disableStats();
		setBlockName(type + "_sign");
		if (type.equals("crimson") || type.equals("warped")) {
			setStepSound(ModSounds.soundNetherWood);
		} else if (type.equals("cherry")) {
			setStepSound(ModSounds.soundCherryWood);
		} else if (type.equals("bamboo")) {
			setStepSound(ModSounds.soundBambooWood);
		} else {
			setStepSound(Block.soundTypeWood);
		}
		if (block != Blocks.planks && standing) { //Only apply this logic to new signs; old ones use a separate item.
			setCreativeTab(EtFuturum.creativeTabBlocks);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return baseBlock.getIcon(side, this.meta);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		if (baseBlock == Blocks.planks) {
			return ModItems.OLD_SIGN_ITEMS[this.meta - 1].get();
		}
		//Only apply this logic to new signs; old ones use a separate item.
		return Item.getItemFromBlock(standingSign);
	}


	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return getItemDropped(0, null, 0);
	}

	public BlockWoodSign getWallSign() {
		return wallSign;
	}

	@Override
	public String getItemIconName() {
		return type + "_sign";
	}
}
