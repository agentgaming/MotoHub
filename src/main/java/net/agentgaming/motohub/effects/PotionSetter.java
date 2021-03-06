package net.agentgaming.motohub.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionSetter implements Runnable {

    public static List<PotionEffect> effects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.SPEED, 1200, 2, true));
        add(new PotionEffect(PotionEffectType.JUMP, 1200, 2, true));
    }};

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            for(PotionEffect pe : p.getActivePotionEffects()) {
                p.removePotionEffect(pe.getType());
            }
            p.addPotionEffects(effects);
        }
    }

}
