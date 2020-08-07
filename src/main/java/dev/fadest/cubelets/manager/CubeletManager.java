package dev.fadest.cubelets.manager;

import dev.fadest.cubelets.Cubelets;
import dev.fadest.cubelets.utils.MathL;
import dev.fadest.cubelets.utils.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CubeletManager {

    private final Cubelets plugin;
    private final HashSet<Location> currentlyUsedCubelets;

    public CubeletManager(Cubelets plugin) {
        this.plugin = plugin;
        this.currentlyUsedCubelets = new HashSet<>();
    }

    public void openCubelet(Player player, Location cubeletLocation) {
        currentlyUsedCubelets.add(cubeletLocation);
        BukkitRunnable buildingTask = new BukkitRunnable() {
            int phase = 0;

            @Override
            public void run() {
                phase++;
                if (handleConstruction(cubeletLocation, phase)) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> spawnArmorStand(player, cubeletLocation), 5L);
                    this.cancel();
                }
            }
        };
        buildingTask.runTaskTimer(plugin, 5, 5L);
    }

    private boolean handleConstruction(Location location, int phase) {
        switch (phase) {
            case 1:

                changeBlockType(location.clone().add(1, -1, 0), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(0, -1, 1), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(-1, -1, 0), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(0, -1, -1), Material.QUARTZ_BLOCK, (byte) 2);
                break;
            case 2:
                changeBlockType(location.clone().add(1, -1, 1), Material.QUARTZ_BLOCK, (byte) 1);
                changeBlockType(location.clone().add(-1, -1, 1), Material.QUARTZ_BLOCK, (byte) 1);
                changeBlockType(location.clone().add(1, -1, -1), Material.QUARTZ_BLOCK, (byte) 1);
                changeBlockType(location.clone().add(-1, -1, -1), Material.QUARTZ_BLOCK, (byte) 1);
                break;
            case 3:
                changeStairMaterial(location.clone().add(0, -1, 2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(1, -1, 2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(-1, -1, 2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(0, -1, -2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(1, -1, -2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(-1, -1, -2), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(2, -1, 0), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(2, -1, -1), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(2, -1, 1), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(-2, -1, 0), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(-2, -1, -1), Material.QUARTZ_STAIRS);
                changeStairMaterial(location.clone().add(-2, -1, 1), Material.QUARTZ_STAIRS);
                break;
            case 4:
                changeBlockType(location.clone().add(2, -1, 2), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(-2, -1, 2), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(2, -1, -2), Material.QUARTZ_BLOCK, (byte) 2);
                changeBlockType(location.clone().add(-2, -1, -2), Material.QUARTZ_BLOCK, (byte) 2);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                changeBlockType(location.clone().add(2, (phase - 5), 2), phase == 8 ? Material.STEP : Material.QUARTZ_BLOCK, phase == 8 ? (byte) 7 : (byte) 2);
                changeBlockType(location.clone().add(-2, (phase - 5), 2), phase == 8 ? Material.STEP : Material.QUARTZ_BLOCK, phase == 8 ? (byte) 7 : (byte) 2);
                changeBlockType(location.clone().add(2, (phase - 5), -2), phase == 8 ? Material.STEP : Material.QUARTZ_BLOCK, phase == 8 ? (byte) 7 : (byte) 2);
                changeBlockType(location.clone().add(-2, (phase - 5), -2), phase == 8 ? Material.STEP : Material.QUARTZ_BLOCK, phase == 8 ? (byte) 7 : (byte) 2);
                if (phase == 8) {
                    return true;
                }
        }
        return false;
    }

    private void changeBlockType(Location location, Material type, byte data) {
        Block block = location.getBlock();
        block.setType(type);
        block.setData(data);
    }

    private void changeStairMaterial(Location location, Material type) {
        Block block = location.getBlock();
        BlockState blockState = block.getState();
        byte data = blockState.getRawData();
        block.setType(type);
        blockState = block.getState();
        blockState.setRawData(data);
        blockState.update();
    }

    private void spawnArmorStand(Player player, Location cubeletLocation) {
        Location armorStandLocation = cubeletLocation.clone();
        armorStandLocation.setYaw(player.getLocation().getYaw() + 180);
        ArmorStand armorStand = createAngelArmorStand(armorStandLocation);
        CraftArmorStand craftArmorStand = (CraftArmorStand) armorStand;
        craftArmorStand.getHandle().noclip = true;
        armorStand.setHeadPose(armorStand.getHeadPose().setX(0.2));
        armorStand.getWorld().strikeLightning(armorStandLocation);
        BukkitRunnable noClipRunnable = new BukkitRunnable() {
            double increaseY = 0;

            @Override
            public void run() {
                increaseY += 0.1;
                if (handleNoClip(armorStand, increaseY)) {
                    craftArmorStand.getHandle().noclip = false;
                    handleAnimation(player, armorStand, cubeletLocation);
                    this.cancel();
                }
            }
        };
        noClipRunnable.runTaskTimer(plugin, 5L, 3L);
        BukkitRunnable followRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!handleFollow(player, armorStand)) {
                    this.cancel();
                }
            }
        };
        followRunnable.runTaskTimer(plugin, 5L, 3L);
    }

    private ArmorStand createAngelArmorStand(Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0.5, -1, 0.5), ArmorStand.class);
        armorStand.setHelmet(getAngelSkull());
        ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestPlateItemMeta = (LeatherArmorMeta) chestPlate.getItemMeta();
        chestPlateItemMeta.setColor(Color.WHITE);
        chestPlateItemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        chestPlate.setItemMeta(chestPlateItemMeta);

        ItemStack leggings = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(Color.WHITE);
        leggingsMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        leggings.setItemMeta(leggingsMeta);

        ItemStack boots = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.WHITE);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        boots.setItemMeta(bootsMeta);

        armorStand.setChestplate(chestPlate);
        armorStand.setLeggings(leggings);
        armorStand.setBoots(boots);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setMarker(false);
        armorStand.setArms(true);
        return armorStand;
    }

    private ItemStack getAngelSkull() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner("00fy");
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    private boolean handleNoClip(ArmorStand armorStand, double increaseY) {
        if (increaseY >= 0.8) {
            return true;
        }
        Location armorStandLocation = armorStand.getLocation();
        armorStandLocation.setY(armorStandLocation.getY() + increaseY);
        armorStand.teleport(armorStandLocation);
        return false;
    }

    private void handleAnimation(Player player, ArmorStand armorStand, Location cubeletLocation) {
        BukkitRunnable movementAnimation = new BukkitRunnable() {
            @Override
            public void run() {
                double currentPosition = armorStand.getHeadPose().getX();
                if (currentPosition <= -0.8) {
                    ArmorStand rewardArmorStand = createRewardArmorStand(armorStand.getLocation());
                    AtomicInteger rotation = new AtomicInteger(5);
                    AtomicBoolean doFall = new AtomicBoolean(false);
                    BukkitRunnable rotationSpeedRunnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (rotation.get() >= 45) {
                                this.cancel();
                                Bukkit.getScheduler().runTaskLater(plugin, () -> doFall.set(true), 50L);
                                return;
                            }

                            rotation.getAndAdd(5);
                        }
                    };
                    rotationSpeedRunnable.runTaskTimerAsynchronously(plugin, 0L, 8L);
                    BukkitRunnable headRotationRunnable = new BukkitRunnable() {
                        final List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                        int currentRotation = -360;

                        @Override
                        public void run() {
                            rewardArmorStand.setHeadPose(new EulerAngle(0, Math.toRadians(currentRotation), 0));
                            currentRotation += rotation.get();
                            addRectLineParticles(cubeletLocation, rewardArmorStand.getLocation());
                            ParticleEffect.PORTAL.display(0.3F, 0.1F, 0.3F, 0, 20,
                                    rewardArmorStand.getLocation().clone().add(0, 1, 0), onlinePlayers);

                            if (doFall.get()) {
                                ParticleEffect.CLOUD.display(0.5F, 0.3F, 0.5F, 0, 50,
                                        armorStand.getLocation().clone().add(0, 1, 0), onlinePlayers);
                                armorStand.remove();
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 0.5F);

                                Item item = rewardArmorStand.getWorld().dropItem(rewardArmorStand.getLocation(), getAngelSkull());
                                item.setVelocity(new Vector(0, 0, 0));
                                item.setPickupDelay(Integer.MAX_VALUE);

                                rewardArmorStand.remove();
                                this.cancel();
                                BukkitRunnable haloParticlesRunnable = new BukkitRunnable() {
                                    private int step = 0;

                                    @Override
                                    public void run() {
                                        step++;
                                        if (step == 50) {
                                            item.remove();
                                            handleDestruction(cubeletLocation);
                                            currentlyUsedCubelets.remove(cubeletLocation);
                                            this.cancel();
                                            return;
                                        }
                                        spawnHaloParticles(item.getLocation());
                                    }
                                };
                                haloParticlesRunnable.runTaskTimer(plugin, 14L, 2L);
                            }
                        }
                    };
                    headRotationRunnable.runTaskTimer(plugin, 0L, 1L);
                    this.cancel();
                } else {
                    armorStand.setHeadPose(armorStand.getHeadPose().setX(armorStand.getHeadPose().getX() - 0.07));
                    armorStand.setRightArmPose(armorStand.getRightArmPose().setZ(armorStand.getRightArmPose().getZ() + 0.16));
                    armorStand.setLeftArmPose(armorStand.getLeftArmPose().setZ(armorStand.getLeftArmPose().getZ() - 0.16));
                }
            }
        };
        movementAnimation.runTaskTimer(plugin, 10L, 2L);
    }

    private ArmorStand createRewardArmorStand(Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0, 2.5, 0), ArmorStand.class);
        armorStand.setHelmet(getRewardSkull());
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setMarker(false);

        return armorStand;
    }


    private ItemStack getRewardSkull() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner("b1ackpearl");
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    private boolean handleFollow(Player player, ArmorStand armorStand) {
        if (armorStand.isDead()) {
            return false;
        }
        Location armorStandLocation = armorStand.getLocation();
        armorStandLocation.setYaw(player.getLocation().getYaw() + 180);
        armorStand.teleport(armorStandLocation);
        return true;
    }

    private void addRectLineParticles(Location startLocation, Location endLocation) {
        spawnRectLineParticle(startLocation.clone().add(2.5, 3, 2.5), endLocation.clone().add(0.3, 0.3, 0.3));
        spawnRectLineParticle(startLocation.clone().add(2.5, 3, -1.5), endLocation.clone().add(0.3, 0.3, -0.2));
        spawnRectLineParticle(startLocation.clone().add(-1.5, 3, 2.5), endLocation.clone().add(-0.3, 0.3, 0.3));
        spawnRectLineParticle(startLocation.clone().add(-1.5, 3, -1.5), endLocation.clone().add(-0.3, 0.3, -0.3));
    }

    private void spawnRectLineParticle(Location startingPoint, Location endLocation) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return;
        double separation = 0.03;
        Vector lineVector = endLocation.toVector().subtract(startingPoint.toVector());

        for (double line = 0; line < 1.2; line += separation) {
            Location point = startingPoint.clone().add(lineVector.getX() * line, lineVector.getY() * line, lineVector.getZ() * line);
            if (point.getY() >= (startingPoint.getY() + 6.5)) {
                break;
            }
            ParticleEffect.ENCHANTMENT_TABLE.display(0.F, 0.F, 0.F, 0, 1, point, players);
        }
    }

    private void handleDestruction(Location location) {
        changeBlockType(location.clone().add(1, -1, 0), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(0, -1, 1), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(-1, -1, 0), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(0, -1, -1), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(1, -1, 1), Material.STONE, (byte) 6);
        changeBlockType(location.clone().add(-1, -1, 1), Material.STONE, (byte) 6);
        changeBlockType(location.clone().add(1, -1, -1), Material.STONE, (byte) 6);
        changeBlockType(location.clone().add(-1, -1, -1), Material.STONE, (byte) 6);
        changeStairMaterial(location.clone().add(0, -1, 2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(1, -1, 2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(-1, -1, 2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(0, -1, -2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(1, -1, -2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(-1, -1, -2), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(2, -1, 0), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(2, -1, -1), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(2, -1, 1), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(-2, -1, 0), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(-2, -1, -1), Material.SMOOTH_STAIRS);
        changeStairMaterial(location.clone().add(-2, -1, 1), Material.SMOOTH_STAIRS);
        changeBlockType(location.clone().add(2, -1, 2), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(-2, -1, 2), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(2, -1, -2), Material.DOUBLE_STEP, (byte) 0);
        changeBlockType(location.clone().add(-2, -1, -2), Material.DOUBLE_STEP, (byte) 0);

        for (int i = 0; i < 4; i++) {
            location.clone().add(2, i, 2).getBlock().setType(Material.AIR);
            location.clone().add(-2, i, 2).getBlock().setType(Material.AIR);
            location.clone().add(2, i, -2).getBlock().setType(Material.AIR);
            location.clone().add(-2, i, -2).getBlock().setType(Material.AIR);
        }
    }

    private void spawnHaloParticles(Location location) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return;
        double slice = 2 * Math.PI / 16;
        for (int i = 0; i < 16; i++) {
            double angle = slice * i;
            double directionX = 0.50 * MathL.cos(angle);
            double directionY = 1;
            double directionZ = 0.50 * MathL.sin(angle);
            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.WHITE),
                    location.clone().add(directionX, directionY, directionZ), players);
        }
    }

    public boolean isCubeletBeingUsing(Location location) {
        return currentlyUsedCubelets.contains(location);
    }
}
