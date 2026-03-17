package com.xioyim.customburntime;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BurnTimeConfig {

    private static final Map<String, Integer> BURN_TIMES = new HashMap<>();
    private static final String CONFIG_FILE = "customburntime.toml";

    public static void load() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        BURN_TIMES.clear();

        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments, empty lines, and section headers
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');
                if (equalsIndex < 0) continue;

                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                // Strip inline comments (e.g. "minecraft:coal" = 1600 # some comment)
                int commentIndex = value.indexOf('#');
                if (commentIndex >= 0) {
                    value = value.substring(0, commentIndex).trim();
                }

                // Remove surrounding quotes from the key
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1).trim();
                }

                // Default to minecraft namespace if no namespace given
                if (!key.contains(":")) {
                    key = "minecraft:" + key;
                }

                try {
                    int burnTime = Integer.parseInt(value);
                    if (burnTime < 0) {
                        CustomBurnTimeMod.LOGGER.warn("Burn time for {} is negative ({}), skipping.", key, burnTime);
                        continue;
                    }
                    BURN_TIMES.put(key, burnTime);
                } catch (NumberFormatException e) {
                    CustomBurnTimeMod.LOGGER.warn("Invalid burn time value for \"{}\": \"{}\" — expected an integer.", key, value);
                }
            }

            CustomBurnTimeMod.LOGGER.info("[CustomBurnTime] Loaded {} burn time entries from config.", BURN_TIMES.size());
        } catch (IOException e) {
            CustomBurnTimeMod.LOGGER.error("[CustomBurnTime] Failed to read config file: {}", configPath, e);
        }
    }

    /**
     * Returns the custom burn time for the given registry name, or null if not configured.
     */
    public static Integer getBurnTime(String registryName) {
        return BURN_TIMES.get(registryName);
    }

    private static void createDefaultConfig(Path path) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("# CustomBurnTime Configuration  |  CustomBurnTime 配置文件");
            writer.println("# Author: XioYim");
            writer.println("#");
            writer.println("# Configure custom burn times for any item here.");
            writer.println("# 在此处自由配置任意物品的燃烧时间。");
            writer.println("#");
            writer.println("# Format | 格式:  \"modid:item_id\" = burn_time_in_ticks");
            writer.println("#");
            writer.println("# Burn time is measured in ticks (20 ticks = 1 second, smelting 1 item = 200 ticks).");
            writer.println("# 燃烧时间以游戏刻（tick）为单位，20 tick = 1 秒，冶炼一个物品默认需要 200 tick。");
            writer.println("# Set to 0 to prevent an item from being used as fuel.");
            writer.println("# 设置为 0 可禁止该物品作为燃料使用。");
            writer.println("#");
            writer.println("# You can omit the namespace for vanilla items (defaults to minecraft:).");
            writer.println("# 原版物品可省略命名空间，默认视为 minecraft: 前缀。");
            writer.println("#   \"coal\" = 1600   ==   \"minecraft:coal\" = 1600");
            writer.println("#");
            writer.println("# Items from other mods use their mod's namespace.");
            writer.println("# 其他 mod 的物品使用对应 mod 的命名空间。");
            writer.println("#   \"thermal:coal_coke\" = 3200");
            writer.println("#");
            writer.println("# Run \"/customburntime reload\" in-game to reload without restarting.");
            writer.println("# 游戏内执行 /customburntime reload 可热重载本配置文件，无需重启。");
            writer.println();
            writer.println("[burn_times]");
            writer.println("# ============================================================");
            writer.println("# 以下为原版默认燃料的燃烧时间（仅供参考，未注释则生效）");
            writer.println("# Vanilla default fuel burn times (listed for reference)");
            writer.println("# ============================================================");
            writer.println();
            writer.println("# --- 顶级燃料 / Top-tier fuels ---");
            writer.println("# \"minecraft:lava_bucket\"        = 20000  # 岩浆桶 Lava Bucket");
            writer.println("# \"minecraft:coal_block\"         = 16000  # 煤炭块 Coal Block");
            writer.println("# \"minecraft:dried_kelp_block\"   = 4001   # 干海带块 Dried Kelp Block");
            writer.println("# \"minecraft:blaze_rod\"          = 2400   # 烈焰棒 Blaze Rod");
            writer.println();
            writer.println("# --- 常规燃料 / Standard fuels ---");
            writer.println("# \"minecraft:coal\"               = 1600   # 煤炭 Coal");
            writer.println("# \"minecraft:charcoal\"           = 1600   # 木炭 Charcoal");
            writer.println();
            writer.println("# --- 原木 / Logs (all wood types) ---");
            writer.println("# \"minecraft:oak_log\"            = 300    # 橡木原木");
            writer.println("# \"minecraft:spruce_log\"         = 300    # 云杉原木");
            writer.println("# \"minecraft:birch_log\"          = 300    # 白桦原木");
            writer.println("# \"minecraft:jungle_log\"         = 300    # 丛林原木");
            writer.println("# \"minecraft:acacia_log\"         = 300    # 金合欢原木");
            writer.println("# \"minecraft:dark_oak_log\"       = 300    # 深色橡木原木");
            writer.println("# \"minecraft:mangrove_log\"       = 300    # 红树原木");
            writer.println("# \"minecraft:cherry_log\"         = 300    # 樱花原木");
            writer.println("# \"minecraft:bamboo_block\"       = 300    # 竹块 Bamboo Block");
            writer.println();
            writer.println("# --- 木板 / Planks ---");
            writer.println("# \"minecraft:oak_planks\"         = 300    # 橡木木板");
            writer.println("# \"minecraft:spruce_planks\"      = 300    # 云杉木板");
            writer.println("# \"minecraft:birch_planks\"       = 300    # 白桦木板");
            writer.println("# \"minecraft:jungle_planks\"      = 300    # 丛林木板");
            writer.println("# \"minecraft:acacia_planks\"      = 300    # 金合欢木板");
            writer.println("# \"minecraft:dark_oak_planks\"    = 300    # 深色橡木木板");
            writer.println("# \"minecraft:mangrove_planks\"    = 300    # 红树木板");
            writer.println("# \"minecraft:cherry_planks\"      = 300    # 樱花木板");
            writer.println("# \"minecraft:bamboo_planks\"      = 300    # 竹木板 Bamboo Planks");
            writer.println();
            writer.println("# --- 木制半砖 / Wooden Slabs ---");
            writer.println("# \"minecraft:oak_slab\"           = 150    # 橡木台阶");
            writer.println("# \"minecraft:spruce_slab\"        = 150    # 云杉台阶");
            writer.println("# \"minecraft:birch_slab\"         = 150    # 白桦台阶");
            writer.println("# \"minecraft:jungle_slab\"        = 150    # 丛林台阶");
            writer.println("# \"minecraft:acacia_slab\"        = 150    # 金合欢台阶");
            writer.println("# \"minecraft:dark_oak_slab\"      = 150    # 深色橡木台阶");
            writer.println("# \"minecraft:mangrove_slab\"      = 150    # 红树台阶");
            writer.println("# \"minecraft:cherry_slab\"        = 150    # 樱花台阶");
            writer.println("# \"minecraft:bamboo_mosaic_slab\" = 150    # 竹马赛克台阶");
            writer.println();
            writer.println("# --- 其他木制品 / Other wooden items ---");
            writer.println("# \"minecraft:crafting_table\"     = 300    # 工作台 Crafting Table");
            writer.println("# \"minecraft:bookshelf\"          = 300    # 书架 Bookshelf");
            writer.println("# \"minecraft:chest\"              = 1500   # 箱子 Chest");
            writer.println("# \"minecraft:trapped_chest\"      = 1500   # 陷阱箱 Trapped Chest");
            writer.println("# \"minecraft:lectern\"            = 1500   # 讲台 Lectern");
            writer.println("# \"minecraft:composter\"          = 300    # 堆肥桶 Composter");
            writer.println("# \"minecraft:barrel\"             = 300    # 木桶 Barrel");
            writer.println("# \"minecraft:cartography_table\"  = 300    # 制图台 Cartography Table");
            writer.println("# \"minecraft:fletching_table\"    = 300    # 制箭台 Fletching Table");
            writer.println("# \"minecraft:smithing_table\"     = 300    # 锻造台 Smithing Table");
            writer.println("# \"minecraft:loom\"               = 300    # 织布机 Loom");
            writer.println("# \"minecraft:note_block\"         = 300    # 音符盒 Note Block");
            writer.println("# \"minecraft:jukebox\"            = 300    # 唱片机 Jukebox");
            writer.println("# \"minecraft:daylight_detector\"  = 300    # 阳光探测器 Daylight Detector");
            writer.println("# \"minecraft:bow\"                = 300    # 弓 Bow");
            writer.println("# \"minecraft:fishing_rod\"        = 300    # 钓鱼竿 Fishing Rod");
            writer.println("# \"minecraft:wooden_sword\"       = 200    # 木剑 Wooden Sword");
            writer.println("# \"minecraft:wooden_pickaxe\"     = 200    # 木镐 Wooden Pickaxe");
            writer.println("# \"minecraft:wooden_axe\"         = 200    # 木斧 Wooden Axe");
            writer.println("# \"minecraft:wooden_shovel\"      = 200    # 木锹 Wooden Shovel");
            writer.println("# \"minecraft:wooden_hoe\"         = 200    # 木锄 Wooden Hoe");
            writer.println("# \"minecraft:stick\"              = 100    # 木棍 Stick");
            writer.println("# \"minecraft:bamboo\"             = 50     # 竹子 Bamboo");
            writer.println();
            writer.println("# ============================================================");
            writer.println("# 在下方添加你的自定义配置 / Add your custom entries below:");
            writer.println("# ============================================================");
            writer.println();
        } catch (IOException e) {
            CustomBurnTimeMod.LOGGER.error("[CustomBurnTime] Failed to create default config file.", e);
        }
    }
}
