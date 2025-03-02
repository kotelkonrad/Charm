package svenhjol.charm.base;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import svenhjol.meson.asm.ClassNameMap;
import svenhjol.meson.asm.MesonClassTransformer;

public class CharmClassTransformer extends MesonClassTransformer
{
    private static final String ASM_HOOKS = "svenhjol/charm/base/ASMHooks";

    static {
        CLASS_MAPPINGS = new ClassNameMap(
            "net/minecraft/util/NonNullList", "fi",
            "net/minecraft/block/BlockDoor", "aqa",
            "net/minecraft/client/renderer/entity/layers/LayerArmorBase", "cbp",
            "net/minecraft/entity/Entity", "vg",
            "net/minecraft/entity/EntityLivingBase", "vp",
            "net/minecraft/entity/boss/EntityWither", "abx",
            "net/minecraft/entity/player/EntityPlayer", "aed",
            "net/minecraft/entity/player/EntityPlayer$SleepResult", "aed$a",
            "net/minecraft/entity/player/EntityPlayerMP", "oq",
            "net/minecraft/entity/player/InventoryPlayer", "aec",
            "net/minecraft/init/Blocks", "aox",
            "net/minecraft/inventory/ContainerBrewingStand", "afu",
            "net/minecraft/inventory/ContainerBrewingStand$Potion", "afu$c",
            "net/minecraft/inventory/ContainerFurnace", "agd",
            "net/minecraft/inventory/ContainerRepair", "afs",
            "net/minecraft/inventory/ContainerRepair$2", "afs$2",
            "net/minecraft/inventory/EntityEquipmentSlot", "vl",
            "net/minecraft/inventory/IInventory", "tv",
            "net/minecraft/inventory/SlotShulkerBox", "agq",
            "net/minecraft/item/Item$ToolMaterial", "ain$a",
            "net/minecraft/item/ItemChorusFruit", "ahk",
            "net/minecraft/potion/Potion", "uz",
            "net/minecraft/server/management/PlayerInteractionManager", "or",
            "net/minecraft/tileentity/TileEntityBeacon", "avh",
            "net/minecraft/tileentity/TileEntityFurnace", "avu",
            "net/minecraft/tileentity/TileEntityShulkerBox", "awb",
            "net/minecraft/util/math/AxisAlignedBB", "bhb",
            "net/minecraft/util/math/BlockPos", "et",
            "net/minecraft/world/gen/structure/MapGenVillage$Start", "bca$a",
            "net/minecraft/world/gen/structure/StructureBoundingBox", "bbg",
            "net/minecraft/world/gen/structure/StructureComponent", "bbx",
            "net/minecraft/world/gen/structure/StructureStart", "bby",
            "net/minecraft/world/gen/structure/StructureVillagePieces$Start", "bcb$k",
            "net/minecraft/world/gen/structure/StructureVillagePieces$Village", "bcb$n",
            "net/minecraft/world/World", "amu",
            "net/minecraft/util/DamageSource", "ur",
            "net/minecraft/item/ItemStack", "aip"
        );

        transformers.put("net.minecraft.item.Item$ToolMaterial", CharmClassTransformer::transformItemToolMaterial);
        transformers.put("net.minecraftforge.items.ItemHandlerHelper", CharmClassTransformer::transformItemHandlerHelper);
        transformers.put("net.minecraftforge.common.ISpecialArmor$ArmorProperties", CharmClassTransformer::transformISpecialArmor);
        transformers.put("net.minecraftforge.common.brewing.BrewingRecipeRegistry", CharmClassTransformer::transformBrewingRecipeRegistry);
        transformers.put("net.minecraft.inventory.ContainerFurnace", CharmClassTransformer::transformContainerFurnace);
        transformers.put("net.minecraft.inventory.ContainerRepair", CharmClassTransformer::transformContainerRepair);
        transformers.put("net.minecraft.inventory.ContainerRepair$2", CharmClassTransformer::transformContainerRepair2);
        transformers.put("net.minecraft.inventory.SlotShulkerBox", CharmClassTransformer::transformSlotShulkerBox);
        transformers.put("net.minecraft.entity.boss.EntityWither", CharmClassTransformer::transformEntityWither);
        transformers.put("net.minecraft.entity.player.EntityPlayer", CharmClassTransformer::transformEntityPlayer);
        transformers.put("net.minecraft.item.ItemChorusFruit", CharmClassTransformer::transformItemChorusFruit);
        transformers.put("net.minecraft.server.management.PlayerInteractionManager", CharmClassTransformer::transformPlayerInteractionManager);
        transformers.put("net.minecraft.client.renderer.entity.layers.LayerArmorBase", CharmClassTransformer::transformLayerArmorBase);
        transformers.put("net.minecraft.world.gen.structure.StructureStart", CharmClassTransformer::transformStructureStart);
        transformers.put("net.minecraft.world.gen.structure.StructureVillagePieces$Village", CharmClassTransformer::transformStructureVillagePiecesVillage);
        transformers.put("net.minecraft.tileentity.TileEntityBeacon", CharmClassTransformer::transformTileEntityBeacon);
        transformers.put("net.minecraft.tileentity.TileEntityFurnace", CharmClassTransformer::transformTileEntityFurnace);
        transformers.put("net.minecraft.tileentity.TileEntityShulkerBox", CharmClassTransformer::transformTileEntityShulkerBox);
    }

    private static byte[] transformPlayerInteractionManager(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm, "PlayerInteractionManager")) return basicClass;
        log("Transforming PlayerInteractionManager");

        MethodSignature tryHarvestBlock = new MethodSignature("tryHarvestBlock", "func_145949_j", "b", "(Lnet/minecraft/util/math/BlockPos;)Z");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(tryHarvestBlock, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD
                && ((VarInsnNode)node).var == 0
                && node.getNext().getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "startCollectingDrops", "(Lnet/minecraft/server/management/PlayerInteractionManager;)V", false));
                method.instructions.insertBefore(node, newInstructions);
                return true;
            }
        )));

        transClass = transform(transClass, Pair.of(tryHarvestBlock, combine(
            (AbstractInsnNode node) -> true,
            (MethodNode method, AbstractInsnNode node) -> {
                for (int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode currentNode = method.instructions.get(i);
                    if (currentNode.getOpcode() == Opcodes.IRETURN) {
                        InsnList newInstructions = new InsnList();
                        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "stopCollectingDrops", "()V", false));
                        method.instructions.insertBefore(currentNode.getPrevious().getPrevious(), newInstructions);
                        i += 1;
                    }
                }
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformContainerFurnace(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm, "ContainerFurnace")) return basicClass;
        log("Transforming ContainerFurnace");

        MethodSignature init = new MethodSignature("<init>", "<init>", "", "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/inventory/IInventory;)V");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(init, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.NEW,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new TypeInsnNode(Opcodes.NEW, "svenhjol/charm/tweaks/inventory/SlotFurnaceInput"));
                    method.instructions.insert(node, newInstructions);
                    method.instructions.remove(node);
                    return true;
                }
        )));

        transClass = transform(transClass, Pair.of(init, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.INVOKESPECIAL && node.getNext().getOpcode() == Opcodes.INVOKEVIRTUAL,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "svenhjol/charm/tweaks/inventory/SlotFurnaceInput", "<init>", "(Lnet/minecraft/inventory/IInventory;III)V", false));
                    method.instructions.insert(node, newInstructions);
                    method.instructions.remove(node);
                    return true;
                }
        )));

        return transClass;
    }

    private static byte[] transformTileEntityFurnace(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"TileEntityFurnace")) return basicClass;
        log("Transforming TileEntityFurnace");

        MethodSignature smeltItem = new MethodSignature("smeltItem", "func_145949_j", "o", "()V");
        MethodSignature isItemValidForSlot = new MethodSignature("isItemValidForSlot", "func_94041_b", "b", "(ILnet/minecraft/item/ItemStack;)Z");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(smeltItem, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ASTORE && ((VarInsnNode)node).var == 2,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "changeSmeltingResult", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
                    newInstructions.add(new VarInsnNode(Opcodes.ASTORE, 2));
                    method.instructions.insert(node, newInstructions);
                    return true;
                }
        )));

        transClass = transform(transClass, Pair.of(isItemValidForSlot, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ILOAD && ((VarInsnNode)node).var == 1,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                    LabelNode label = new LabelNode();
                    newInstructions.add(new JumpInsnNode(Opcodes.IFNE, label));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canSmelt", "(Lnet/minecraft/item/ItemStack;)Z", false));
                    newInstructions.add(new InsnNode(Opcodes.IRETURN));
                    newInstructions.add(label);
                    method.instructions.insertBefore(node, newInstructions);
                    return true;
                }
        )));

        return transClass;
    }

    private static byte[] transformItemChorusFruit(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ItemChorusFruit")) return basicClass;
        log("Transforming ItemChorusFruit");

        MethodSignature onItemUseFinish = new MethodSignature("onItemUseFinish", "func_77654_b", "a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;");

        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(onItemUseFinish, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.IFNE,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "onChorusFruitEaten", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                    newInstructions.add(new JumpInsnNode(Opcodes.IFNE, ((JumpInsnNode)node).label));
                    method.instructions.insert(node, newInstructions);
                    return true;
                }
        )));

        return transClass;
    }

    private static byte[] transformItemToolMaterial(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ItemToolMaterial")) return basicClass;
        log("Transforming ItemToolMaterial");

        MethodSignature getMaxUses = new MethodSignature("getMaxUses", "func_77997_a", "a", "()I");
        MethodSignature getEfficiency = new MethodSignature("getEfficiency", "func_77998_b", "b", "()F");
        MethodSignature getAttackDamage = new MethodSignature("getAttackDamage", "func_78000_c", "c", "()F");
        MethodSignature getHarvestLevel = new MethodSignature("getHarvestLevel", "func_77996_d", "d", "()I");
        MethodSignature getEnchantability = new MethodSignature("getEnchantability", "func_77995_e", "e", "()I");

        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(getMaxUses, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList before = new InsnList();
                InsnList after = new InsnList();

                before.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(node, before);

                after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getToolMaterialMaxUses", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
                method.instructions.insert(node, after);
                return true;
            }
        )));

        transClass = transform(transClass, Pair.of(getEfficiency, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList before = new InsnList();
                InsnList after = new InsnList();

                before.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(node, before);

                after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getToolMaterialEfficiency", "(Lnet/minecraft/item/Item$ToolMaterial;F)F", false));
                method.instructions.insert(node, after);
                return true;
            }
        )));

        transClass = transform(transClass, Pair.of(getAttackDamage, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList before = new InsnList();
                InsnList after = new InsnList();

                before.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(node, before);

                after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getToolMaterialAttackDamage", "(Lnet/minecraft/item/Item$ToolMaterial;F)F", false));
                method.instructions.insert(node, after);
                return true;
            }
        )));

        transClass = transform(transClass, Pair.of(getHarvestLevel, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList before = new InsnList();
                InsnList after = new InsnList();

                before.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(node, before);

                after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getToolMaterialHarvestLevel", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
                method.instructions.insert(node, after);
                return true;
            }
        )));

        transClass = transform(transClass, Pair.of(getEnchantability, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.GETFIELD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList before = new InsnList();
                InsnList after = new InsnList();

                before.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(node, before);

                after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getToolMaterialEnchantability", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
                method.instructions.insert(node, after);
                return true;
            }
        )));

        return transClass;
    }

    private static int countTransformTileEntityBeacon;

    private static byte[] transformTileEntityBeacon(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"TileEntityBeacon")) return basicClass;
        log("Transforming TileEntityBeacon");

        MethodSignature addEffectsToPlayers = new MethodSignature("addEffectsToPlayers", "func_146000_x", "E", "()V");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(addEffectsToPlayers, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 9 && ++countTransformTileEntityBeacon == 2,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "field_145850_b" : "world", "Lnet/minecraft/world/World;"));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 8));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "field_146013_m" : "primaryEffect", "Lnet/minecraft/potion/Potion;"));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "field_146010_n" : "secondaryEffect", "Lnet/minecraft/potion/Potion;"));
                    newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "addBeaconEffect", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/potion/Potion;Lnet/minecraft/potion/Potion;II)V", false));
                    method.instructions.insertBefore(node, newInstructions);
                    return true;
                }
        )));

        return transClass;
    }

    private static byte[] transformTileEntityShulkerBox(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"TileEntityShulkerBox")) return basicClass;
        log("Transforming TileEntityShulkerBox");

        MethodSignature canInsertItem = new MethodSignature("canInsertItem", "func_180462_a", "a", "(ILnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumFacing;)Z");

        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(canInsertItem, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 2,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canInsertItemIntoShulkerBox", "(Lnet/minecraft/item/ItemStack;)Z", false));
                newInstructions.add(new InsnNode(Opcodes.IRETURN));
                method.instructions = newInstructions;
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformSlotShulkerBox(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"SlotShulkerBox")) return basicClass;
        log("Transforming SlotShulkerBox");

        MethodSignature isItemValid = new MethodSignature("isItemValid", "func_180462_a", "a", "(Lnet/minecraft/item/ItemStack;)Z");

        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(isItemValid, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 1,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canInsertItemIntoShulkerBox", "(Lnet/minecraft/item/ItemStack;)Z", false));
                newInstructions.add(new InsnNode(Opcodes.IRETURN));
                method.instructions = newInstructions;
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformItemHandlerHelper(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ItemHandlerHelper")) return basicClass;
        log("Transforming ItemHandlerHelper");

        MethodSignature insertItem = new MethodSignature("insertItem", "insertItem", "", "(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(insertItem, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 0,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canInsertItemIntoShulkerBox", "(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;)Z", false));
                LabelNode label = new LabelNode();
                newInstructions.add(new JumpInsnNode(Opcodes.IFNE, label));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new InsnNode(Opcodes.ARETURN));
                newInstructions.add(label);
                method.instructions.insertBefore(node, newInstructions);
                return true;
            }
        )));

        return transClass;
    }

    private static int countTransformISpecialArmor;

    private static byte[] transformISpecialArmor(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ISpecialArmor")) return basicClass;
        log("Transforming ISpecialArmor");

        MethodSignature applyArmor = new MethodSignature("applyArmor", "applyArmor", "", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(applyArmor, combine(
            (AbstractInsnNode node) -> (node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 1)
                && (node.getNext().getOpcode() == Opcodes.ILOAD && ((VarInsnNode)node.getNext()).var == 12)
                && ++countTransformISpecialArmor == 2,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 12));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/NonNullList", "get", "(I)Ljava/lang/Object;", false));
                newInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/item/ItemStack"));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canArmorBeSalvaged", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;)V", false));
                method.instructions.insertBefore(node, newInstructions);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformBrewingRecipeRegistry(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"BrewingRecipeRegistry")) return basicClass;
        log("Transforming BrewingRecipeRegistry");

        MethodSignature isValidInput = new MethodSignature("isValidInput", "isValidInput", "", "(Lnet/minecraft/item/ItemStack;)Z");

        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(isValidInput, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.IF_ICMPEQ,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, ((JumpInsnNode)node).label));
                    method.instructions.insertBefore(node, newInstructions);
                    method.instructions.remove(node);
                    return true;
                }
        )));

        return transClass;
    }

    private static int countTransformContainerRepair;

    private static byte[] transformContainerRepair(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ContainerRepair")) return basicClass;
        log("Transforming ContainerRepair");

        MethodSignature updateRepairOutput = new MethodSignature("updateRepairOutput", "func_82848_d", "e", "()V");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(updateRepairOutput, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ICONST_0
                && node.getPrevious().getOpcode() == Opcodes.ALOAD
                && ((VarInsnNode)node.getPrevious()).var == 0
                && ++countTransformContainerRepair == 2,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new InsnNode(Opcodes.ICONST_1));
                method.instructions.insert(node, newInstructions);
                method.instructions.remove(node);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformContainerRepair2(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"ContainerRepair2")) return basicClass;
        log("Transforming ContainerRepair2");

        MethodSignature canTakeStack = new MethodSignature("canTakeStack", "func_82869_a", "a", "(Lnet/minecraft/entity/player/EntityPlayer;)Z");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(canTakeStack, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.IFLE,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new JumpInsnNode(Opcodes.IFLT, ((JumpInsnNode)node).label));
                method.instructions.insertBefore(node, newInstructions);
                method.instructions.remove(node);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformLayerArmorBase(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"LayerArmorBase")) return basicClass;
        log("Transforming LayerArmorBase");

        MethodSignature renderArmorLayer = new MethodSignature("renderArmorLayer", "func_188361_a", "a", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V");
        byte[] transClass = basicClass;

        // skip render if armor is flagged as invisible
        transClass = transform(transClass, Pair.of(renderArmorLayer, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ASTORE,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 10));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "isArmorInvisible", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Z", false));
                LabelNode label = new LabelNode();
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
                newInstructions.add(new InsnNode(Opcodes.RETURN));
                newInstructions.add(label);

                method.instructions.insert(node, newInstructions);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformEntityPlayer(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"EntityPlayer")) return basicClass;
        log("Transforming EntityPlayer");

        MethodSignature getArmorVisibility = new MethodSignature("getArmorVisibility", "func_82243_bO", "cW", "()F");

        byte[] transClass = basicClass;

        // don't increase mob visibility if armor is flagged as invisible
        transClass = transform(transClass, Pair.of(getArmorVisibility, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.IINC,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "isArmorInvisible", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Z", false));
                LabelNode label = new LabelNode();
                newInstructions.add(new JumpInsnNode(Opcodes.IFNE, label));
                newInstructions.add(new IincInsnNode(1, 1));
                newInstructions.add(label);

                method.instructions.insertBefore(node, newInstructions);
                method.instructions.remove(node);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformEntityWither(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"EntityWither")) return basicClass;
        log("Transforming EntityWither");

        MethodSignature canDestroyBlock = new MethodSignature("canDestroyBlock", "func_181033_a", "a", "(Lnet/minecraft/block/Block;)Z");
        byte[] transClass = basicClass;

        transClass = transform(transClass, Pair.of(canDestroyBlock, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 0,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "canWitherDestroyBlock", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", false));
                newInstructions.add(new VarInsnNode(Opcodes.ASTORE, 0));
                method.instructions.insertBefore(node, newInstructions);
                return true;
            }
        )));

        return transClass;
    }

    private static byte[] transformStructureVillagePiecesVillage(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"StructureVillagePieces")) return basicClass;
        log("Transforming StructureVillagePieces$Village");

        MethodSignature biomeDoor = new MethodSignature("biomeDoor", "func_189925_i", "i", "()Lnet/minecraft/block/BlockDoor;");
        return transform(basicClass, Pair.of(biomeDoor, combine(
            (AbstractInsnNode node) -> node.getOpcode() == Opcodes.ALOAD,
            (MethodNode method, AbstractInsnNode node) -> {
                InsnList newInstructions = new InsnList();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/structure/StructureVillagePieces$Village", "startPiece", "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;"));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "villageDoorsForBiome", "(Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;)Lnet/minecraft/block/BlockDoor;", false));
                newInstructions.add(new InsnNode(Opcodes.ARETURN));
                method.instructions = newInstructions;
                return true;
            }
        )));
    }

    private static byte[] transformStructureStart(byte[] basicClass)
    {
        if (!checkTransformers(CharmLoadingPlugin.configAsm,"StructureStart")) return basicClass;
        log("Transforming StructureStart");

        byte[] transformClass = basicClass;
        MethodSignature generateStructure = new MethodSignature("generateStructure", "func_75068_a", "a", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)V");

        // replace the addComponentParts() call with custom version so we can call the items after generating a structure
        transformClass = transform(transformClass, Pair.of(generateStructure, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && checkDesc(((MethodInsnNode)node).desc, "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z")
                ,
                (MethodNode method, AbstractInsnNode node) -> {
                    InsnList newInstructions = new InsnList();
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "addComponentParts", "(Lnet/minecraft/world/gen/structure/StructureComponent;Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z", false));
                    method.instructions.insert(node, newInstructions);
                    method.instructions.remove(node);
                    return true;
                }
        )));
        return transformClass;
    }
}