package dev.wplugins.waze.gerementions.punish.service;


import dev.wplugins.waze.gerementions.model.Model;

import java.util.List;

public interface PunishServiceSpigot extends Model<String, PunishSpigot> {

    List<PunishSpigot> getPunishes();

}
