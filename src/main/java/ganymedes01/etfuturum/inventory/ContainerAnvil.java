package ganymedes01.etfuturum.inventory;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerAnvil extends ContainerRepair {

	private final EntityPlayer player;
	private IInventory inputSlots = null;
	private IInventory outputSlot = null;
	private String repairedItemName;

	public ContainerAnvil(EntityPlayer player, World world, int x, int y, int z) {
		super(player.inventory, world, x, y, z, player);
		this.player = player;

		inputSlots = ReflectionHelper.getPrivateValue(ContainerRepair.class, this, "inputSlots", "field_82853_g");
		outputSlot = ReflectionHelper.getPrivateValue(ContainerRepair.class, this, "outputSlot", "field_82852_f");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateRepairOutput() {
		ItemStack itemstack = inputSlots.getStackInSlot(0);
		maximumCost = 1;
		int i = 0;
		byte b0 = 0;
		byte b1 = 0;

		if (itemstack == null) {
			outputSlot.setInventorySlotContents(0, null);
			maximumCost = 0;
		} else {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = inputSlots.getStackInSlot(1);
			Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			boolean flag7 = false;
			int i2 = b0 + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
			stackSizeToBeUsedInRepair = 0;
			int j;

			if (itemstack2 != null) {
				if (!ForgeHooks.onAnvilChange(this, itemstack, itemstack2, outputSlot, repairedItemName, i2))
					return;
				flag7 = itemstack2.getItem() == Items.enchanted_book && Items.enchanted_book.func_92110_g(itemstack2).tagCount() > 0;
				int k;
				int l;

				if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
					j = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);

					if (j <= 0) {
						outputSlot.setInventorySlotContents(0, null);
						maximumCost = 0;
						return;
					}

					for (k = 0; j > 0 && k < itemstack2.stackSize; ++k) {
						l = itemstack1.getItemDamage() - j;
						itemstack1.setItemDamage(l);
						++i;
						j = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
					}

					stackSizeToBeUsedInRepair = k;
				} else {
					if (!flag7 && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable())) {
						outputSlot.setInventorySlotContents(0, null);
						maximumCost = 0;
						return;
					}

					int j1;

					if (itemstack1.isItemStackDamageable() && !flag7) {
						j = itemstack.getMaxDamage() - itemstack.getItemDamage();
						k = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
						l = k + itemstack1.getMaxDamage() * 12 / 100;
						int i1 = j + l;
						j1 = itemstack1.getMaxDamage() - i1;

						if (j1 < 0)
							j1 = 0;

						if (j1 < itemstack1.getItemDamage()) {
							itemstack1.setItemDamage(j1);
							i += 2;
						}
					}

					Map<Integer, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					Iterator<Integer> iterator1 = map1.keySet().iterator();

					while (iterator1.hasNext()) {
						l = iterator1.next().intValue();
						Enchantment enchantment = Enchantment.enchantmentsList[l];

						if (enchantment != null) {
							j1 = map.containsKey(Integer.valueOf(l)) ? map.get(Integer.valueOf(l)).intValue() : 0;
							int k1 = map1.get(Integer.valueOf(l)).intValue();
							int k2;

							if (j1 == k1) {
								++k1;
								k2 = k1;
							} else
								k2 = Math.max(k1, j1);

							k1 = k2;
							boolean flag8 = enchantment.canApply(itemstack);

							if (player.capabilities.isCreativeMode || itemstack.getItem() == Items.enchanted_book)
								flag8 = true;

							Iterator<Integer> iterator = map.keySet().iterator();

							while (iterator.hasNext()) {
								int l1 = iterator.next().intValue();

								Enchantment e2 = Enchantment.enchantmentsList[l1];
								if (l1 != l && !(enchantment.canApplyTogether(e2) && e2.canApplyTogether(enchantment))) //Forge BugFix: Let Both enchantments veto being together
								{
									flag8 = false;
									++i;
								}
							}

							if (flag8) {
								if (k1 > enchantment.getMaxLevel())
									k1 = enchantment.getMaxLevel();

								map.put(Integer.valueOf(l), Integer.valueOf(k1));
								int j2 = 0;

								switch (enchantment.getWeight()) {
									case 1:
										j2 = 8;
										break;
									case 2:
										j2 = 4;
									case 3:
									case 4:
									case 6:
									case 7:
									case 8:
									case 9:
									default:
										break;
									case 5:
										j2 = 2;
										break;
									case 10:
										j2 = 1;
								}

								if (flag7)
									j2 = Math.max(1, j2 / 2);

								i += j2 * k1;
							}
						}
					}
				}
			}

			if (flag7 && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2))
				itemstack1 = null;

			if (StringUtils.isBlank(repairedItemName)) {
				if (itemstack.hasDisplayName()) {
					b1 = 1;
					i += b1;
					itemstack1.func_135074_t();
				}
			} else if (!repairedItemName.equals(itemstack.getDisplayName())) {
				b1 = 1;
				i += b1;
				itemstack1.setStackDisplayName(repairedItemName);
			}

			maximumCost = i2 + i;

			if (i <= 0)
				itemstack1 = null;

			if (b1 == i && b1 > 0 && maximumCost >= 40)
				maximumCost = 39;

			if (maximumCost >= 40 && !player.capabilities.isCreativeMode)
				itemstack1 = null;

			if (itemstack1 != null) {
				j = itemstack1.getRepairCost();

				if (itemstack2 != null && j < itemstack2.getRepairCost())
					j = itemstack2.getRepairCost();

				j = j * 2 + 1;
				itemstack1.setRepairCost(j);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}

			outputSlot.setInventorySlotContents(0, itemstack1);
			detectAndSendChanges();
		}
	}

	@Override
	public void updateItemName(String name) {
		repairedItemName = name;
		super.updateItemName(name);
	}
}