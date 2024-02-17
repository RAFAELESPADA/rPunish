package dev.wplugins.waze.gerementions.punish.service.impl;


import dev.wplugins.waze.gerementions.punish.service.PunishServiceSpigot;
import dev.wplugins.waze.gerementions.punish.service.PunishSpigot;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class PunishServiceImpl2 implements PunishServiceSpigot {

    private final List<PunishSpigot> punishSet;

    public PunishServiceImpl2() {
        punishSet = new LinkedList<>();
    }

    @Override
    public void create(PunishSpigot punish) {
        punishSet.add(punish);
    }

    @Override
    public void remove(String integer) {
        punishSet.remove(get(integer));
    }

    @Override
    public PunishSpigot get(String integer) {
        return search(integer).filter(punish -> punish.getId().equals(integer)).findFirst().orElse(null);
    }

    @Override
    public Stream<PunishSpigot> search(String integer) {
        return punishSet.stream().filter(punish -> punish.getId().equals(integer));
    }

    @Override
    public List<PunishSpigot> getPunishes() {
        return punishSet;
    }
}

