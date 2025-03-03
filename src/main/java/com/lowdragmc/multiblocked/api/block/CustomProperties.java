package com.lowdragmc.multiblocked.api.block;

import com.lowdragmc.multiblocked.Multiblocked;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;

/**
 * Author: KilaBash
 * Date: 2022/04/27
 * Description:
 */
public class CustomProperties {
    public boolean isOpaque;
    public float destroyTime;
    public float explosionResistance;
    public int harvestLevel;
    public int lightEmissive;
    public float speedFactor;
    public float jumpFactor;
    public float friction;
    public boolean hasCollision;
    public String tabGroup;
    public int stackSize;

    public CustomProperties() {
        this.isOpaque = true;
        this.destroyTime = 1.5f;
        this.explosionResistance = 6f;
        this.harvestLevel = 1;
        this.lightEmissive = 0;
        this.speedFactor = 1f;
        this.jumpFactor = 1f;
        this.friction = 0.6f;
        this.hasCollision = true;
        this.tabGroup = "multiblocked.all";
        this.stackSize = 64;
    }

    public AbstractBlock.Properties createBlock() {
        AbstractBlock.Properties properties = AbstractBlock.Properties.of(Material.METAL);
        if (!isOpaque) {
            properties.noOcclusion();
        }
        if (!hasCollision) {
            properties.noCollission();
        }
        properties.strength(destroyTime, explosionResistance)
                .sound(SoundType.STONE)
                .harvestLevel(harvestLevel)
                .speedFactor(speedFactor)
                .jumpFactor(jumpFactor)
                .friction(friction)
                .lightLevel(s->lightEmissive)
                .harvestTool(ToolType.PICKAXE);
        return properties;
    }

    public Item.Properties createItem() {
        Item.Properties properties = new Item.Properties().stacksTo(stackSize);
        if (tabGroup != null) {
            for (ItemGroup tab : ItemGroup.TABS) {
                if (tab.getRecipeFolderName().equals(tabGroup)) {
                    properties.tab(tab);
                    break;
                }
            }
        }
        return properties;
    }
}
