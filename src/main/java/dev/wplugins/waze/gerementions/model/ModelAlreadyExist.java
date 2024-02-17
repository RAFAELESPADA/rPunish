package dev.wplugins.waze.gerementions.model;

public class ModelAlreadyExist extends Exception {

    public ModelAlreadyExist(){
        super("Este modelo já existe.");
    }

}
