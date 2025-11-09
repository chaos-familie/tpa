package de.chaosfamilie.minecraft.tpa;

import org.bukkit.Location;

import java.util.UUID;

public record TpRequest(UUID requester, UUID target, long created, Location location, String server) {
}
