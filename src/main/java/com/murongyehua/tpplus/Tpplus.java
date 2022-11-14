package com.murongyehua.tpplus;

import com.murongyehua.tpplus.common.LogUtil;
import com.murongyehua.tpplus.common.TpInfo;
import com.murongyehua.tpplus.listener.InventoryClickListener;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class Tpplus extends JavaPlugin {

    public static Map<String, TpInfo> tpList = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // 加载配置
        loadConfig();
        // 注册监听器
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(),this);
        LogUtil.log("tp plus 已装载");
    }

    @Override
    public void onDisable() {
        InventoryClickEvent.getHandlerList().unregister(this);
        LogUtil.log("tp plus 已卸载");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("tpplus") && getConfig().getBoolean("tpplus.enable")) {
            try {
                if (!judgeLocation(sender)) {
                    sendMsg((Player) sender, "你必须在合格且完整的传送阵上进行相关操作");
                    return true;
                }
                // 可以设置 /tpplus [子命令] [*名称] [*目标点]
                if (args.length == 0) {
                    // 没有参数 触发传送
                    this.tp(sender);
                }
                // 参数解析
                if (args.length == 1) {
                    // 只有一个参数
                    switch (args[0]) {
                        case "list":
                            // 查看当前所有传送点的名字
                            if (tpList.size() == 0) {
                                sendMsg((Player) sender, "暂无传送阵");
                                break;
                            }
                            sendMsg((Player) sender, String.join(",", tpList.keySet()));
                            break;
                        case "show":
                            // 查看当前所处的传送点的名字
                            TpInfo tpInfo = getCurrentTpInfo(sender);
                            if (tpInfo == null) {
                                sendMsg((Player) sender, "当前不在传送阵！！");
                            }
                            break;
                        case "help":
                            // 查看使用帮助
                            help(sender);
                            break;
                        default:
                            sendMsg((Player) sender, "不支持的指令，/tpplus help查看使用帮助");
                            break;
                    }
                }
                if (args.length == 2) {
                    // 两个参数 认为是 set [名字] 或者 link [名字] 如果不是则不支持
                    switch (args[0]) {
                        case "set":
                            Player player = (Player) sender;
                            String name = args[1];
                            if (tpList.get(name) != null) {
                                sendMsg(player, "已存在同名传送阵，请修改后重试");
                                break;
                            }
                            TpInfo tpInfo = new TpInfo();
                            tpInfo.setName(name);
                            Block block = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(player.getLocation());
                            tpInfo.setLocation(String.format("%s %s %s", block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
                            tpInfo.setCanTpLocation(new ArrayList<>());
                            // 创建文件
                            File file = new File(getDataFolder().getAbsolutePath() + File.separator + name + ".yml");
                            file.createNewFile();
                            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                            configuration.set("tpinfo.location", tpInfo.getLocation());
                            configuration.set("tpinfo.canTpLocation", StringUtils.join(tpInfo.getCanTpLocation(), ","));
                            configuration.save(file);
                            tpList.put(tpInfo.getName(), tpInfo);
                            break;
                        case "link":
                            TpInfo linkTpInfo = this.getCurrentTpInfo(sender);
                            if (linkTpInfo == null) {
                                sendMsg((Player) sender, "你当前不在传送阵，无法进行此操作");
                                break;
                            }
                            String targetName = args[1];
                            TpInfo linkTargetTpInfo = tpList.get(targetName);
                            if (linkTargetTpInfo == null) {
                                sendMsg((Player) sender, "目标传送阵不存在，请确认后再试");
                                break;
                            }
                            if (linkTpInfo.getCanTpLocation().contains(linkTargetTpInfo.getName())) {
                                sendMsg((Player) sender, "当前传送阵已经可以传送目标传送阵，请勿重复操作");
                                break;
                            }
                            linkTpInfo.getCanTpLocation().add(linkTargetTpInfo.getName());
                            // 修改文件
                            File updateFile = new File(getDataFolder().getAbsolutePath() + File.separator + linkTpInfo.getName() + ".yml");
                            FileConfiguration updateConfig = YamlConfiguration.loadConfiguration(updateFile);
                            updateConfig.set("tpinfo.canTpLocation", StringUtils.join(linkTpInfo.getCanTpLocation(), ","));
                            updateConfig.save(updateFile);
                            break;
                        default:
                            sendMsg((Player) sender, "不支持的指令，/tpplus help查看使用帮助");
                            break;
                    }
                }
                if (args.length == 3) {
                    Player player = (Player) sender;
                    // 三个参数 认为是 set [名字] [目标点名字]
                    if (!"set".equals(args[0])) {
                        sendMsg(player, "不支持的指令，/tpplus help查看使用帮助");
                    }
                    String newName = args[1];
                    String targetName = args[2];
                    if (tpList.get(newName) != null) {
                        sendMsg(player, "已存在同名传送阵，请修改后重试");
                        return true;
                    }
                    if (tpList.get(targetName) == null) {
                        sendMsg(player, "目标传送阵不存在，请确认后重试");
                        return true;
                    }
                    TpInfo tpInfo = new TpInfo();
                    tpInfo.setName(newName);
                    Block block = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(player.getLocation());
                    tpInfo.setLocation(String.format("%s %s %s", block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
                    tpInfo.setCanTpLocation(new ArrayList<String>(){{add(targetName);}});
                    // 创建文件
                    File file = new File(getDataFolder().getAbsolutePath() + File.separator + newName + ".yml");
                    file.createNewFile();
                    FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    configuration.set("tpinfo.location", tpInfo.getLocation());
                    configuration.set("tpinfo.canTpLocation", StringUtils.join(tpInfo.getCanTpLocation(), ","));
                    configuration.save(file);
                    tpList.put(tpInfo.getName(), tpInfo);
                }
            }catch (Exception e) {
                LogUtil.log(e.getMessage());
                sendMsg((Player) sender, "出错了！请联系管理人员！");
            }

        }
        return true;
    }

    private void help(CommandSender sender) {
        Player player = (Player) sender;
        sendMsg(player, "/tpplus 触发传送");
        sendMsg(player, "/tpplus list 查看当前所有传送阵");
        sendMsg(player, "/tpplus show 显示当前传送阵名称");
        sendMsg(player, "/tpplus set [名称1] 在当前位置创建一个名字为名称1的传送阵");
        sendMsg(player, "/tpplus link [名称2] 将当前传送阵与名字为名称2的传送阵连接起来(单向)");
        sendMsg(player, "/tpplus set [名称1] [名称2] 在当前位置创建一个名字为名称1的传送阵并同时连接名称2传送阵");
        sendMsg(player, "/tpplus help 查看帮助");
    }

    private TpInfo getCurrentTpInfo(CommandSender sender) {
        // 获取当前位置
        Player player = (Player) sender;
        Block block = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(player.getLocation());
        Location blockLocation = block.getLocation();
        String location = String.format("%s %s %s", blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
        for (TpInfo tpInfo : tpList.values()) {
            if (tpInfo.getLocation().equals(location)) {
                return tpInfo;
            }
        }
        return null;
    }

    private void tp(CommandSender sender) {
        Player player = (Player) sender;
        TpInfo tpInfo = this.getCurrentTpInfo(sender);
        if (tpInfo == null) {
            sendMsg(player, "当前不在传送阵，无法传送！！");
            return;
        }
        List<String> canList = tpInfo.getCanTpLocation();
        if (canList.size() == 0) {
            sendMsg(player, "当前传送阵还未与其他传送阵打通传送通道");
            return;
        }
        if (canList.size() == 1) {
            TpInfo canTp = tpList.get(canList.get(0));
            if (canTp == null) {
                sendMsg(player, "目标传送阵不存在或已被摧毁");
                return;
            }
            player.chat(String.format("/tp %s", canTp.getLocation()));
        }
        if (canList.size() > 1) {
            // 开启选择
            Inventory inv = Bukkit.createInventory(null, 54, "选择目标传送阵");
            for (String tpId : canList) {
                ItemStack is = new ItemStack(Material.COMPASS);
                ItemMeta im = is.getItemMeta();
                assert im != null;
                im.setDisplayName(tpId);
                im.setLore(Collections.singletonList("点击传送"));
                is.setItemMeta(im);
                inv.addItem(is);
            }
            player.openInventory(inv);
        }
    }

    /**
     * 读取配置-可重复执行
     */
    private void loadConfig() {
        tpList.clear();
        File[] files = getDataFolder().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().equals("config.yml")) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String location = config.getString("tpinfo.location");
                    String canTpLocation = config.getString("tpinfo.canTpLocation");
                    TpInfo tpInfo = new TpInfo();
                    tpInfo.setName(file.getName().split("\\.")[0]);
                    tpInfo.setLocation(location);
                    if (canTpLocation == null || canTpLocation.length() == 0) {
                        tpInfo.setCanTpLocation(new ArrayList<>());
                    } else {
                        String[] tpIds = canTpLocation.split(",");
                        tpInfo.setCanTpLocation(new ArrayList<>(Arrays.asList(tpIds)));
                    }
                    tpList.put(file.getName().split("\\.")[0], tpInfo);
                }
            }
        }
    }



    /**
     * 判断位置条件
     *
     * @param sender
     */
    private boolean judgeLocation(CommandSender sender) {
        Player player = (Player) sender;
        Block block = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
        // 钻石块
        if (!block.getType().equals(Material.IRON_BLOCK)) {
            return false;
        }
        // 钻石块附近有岩浆
        // 前
        Block beforeBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(
                block.getLocation().getBlockX() + 1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        if (beforeBlock.getType().equals(Material.LAVA)) {
            return true;
        }
        // 后
        Block afterBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(
                block.getLocation().getBlockX() - 1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        if (afterBlock.getType().equals(Material.LAVA)) {
            return true;
        }
        // 左
        Block leftBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(
                block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 1);
        if (leftBlock.getType().equals(Material.LAVA)) {
            return true;
        }
        // 右
        Block rightBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(
                block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() - 1);
        if (rightBlock.getType().equals(Material.LAVA)) {
            return true;
        }
        // 下
        Block downBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(
                block.getLocation().getBlockX(), block.getLocation().getBlockY() - 1, block.getLocation().getBlockZ());
        if (downBlock.getType().equals(Material.LAVA)) {
            return true;
        }
        return false;
    }

    /**
     * 发送提示
     *
     * @param player 收到提示的玩家
     * @param msg    提示内容
     */
    public void sendMsg(Player player, String msg) {
        player.sendMessage(msg);
    }


}
