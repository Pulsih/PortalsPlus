package me.pulsi_.portalsplus.objects;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PortalEditor {

    private Portal editingPortal;
    private List<Location> portalLocationsHolder;
    private boolean isEditingPortal, isChangingParticles, isPlacingLocations, isPlacingDestination;

    public Portal getEditingPortal() {
        return editingPortal;
    }

    public void setEditingPortal(Portal editingPortal) {
        this.editingPortal = editingPortal;
    }

    public List<Location> getPortalLocationsHolder() {
        return portalLocationsHolder == null ? new ArrayList<>() : portalLocationsHolder;
    }

    public void setPortalLocationsHolder(List<Location> portalLocationsHolder) {
        this.portalLocationsHolder = portalLocationsHolder;
    }

    public boolean isEditingPortal() {
        return isEditingPortal;
    }

    public void setEditingPortal(boolean editingPortal) {
        isEditingPortal = editingPortal;
    }

    public boolean isChangingParticles() {
        return isChangingParticles;
    }

    public void setChangingParticles(boolean changingParticles) {
        isChangingParticles = changingParticles;
    }

    public boolean isPlacingLocations() {
        return isPlacingLocations;
    }

    public void setPlacingLocations(boolean placingLocationsMode) {
        isPlacingLocations = placingLocationsMode;
    }

    public boolean isPlacingDestination() {
        return isPlacingDestination;
    }

    public void setPlacingDestination(boolean placingDestination) {
        isPlacingDestination = placingDestination;
    }
}