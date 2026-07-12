package model;

/**
 * IBuilder interface for in case we want to use a different builder implementation in the future. 
 */
public interface IBuilder {
    Order build();
}