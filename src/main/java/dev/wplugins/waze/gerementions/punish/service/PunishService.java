package dev.wplugins.waze.gerementions.punish.service;

import dev.wplugins.waze.gerementions.model.Model;
import dev.wplugins.waze.gerementions.punish.Punish;

import java.util.List;

public interface PunishService extends Model<String, Punish> {

    List<Punish> getPunishes();

}
