/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author Glenn Sacks
 */
public class IdentifierMetaData {
    
    private Boolean storesLowerCaseIds = true; //default
    private Boolean storesMixedCaseIds = false;
    private Boolean storesUpperCaseIds = false;
    
    public Boolean getStoresLowerCaseIds() {
        return storesLowerCaseIds;
    }

    public void setStoresLowerCaseIds(Boolean storesLowerCaseIds) {
        this.storesLowerCaseIds = storesLowerCaseIds;
        if (storesLowerCaseIds){
            this.storesUpperCaseIds = false;
            this.storesMixedCaseIds = false;
        }
    }

    public Boolean getStoresMixedCaseIds() {
        return storesMixedCaseIds;
    }

    public void setStoresMixedCaseIds(Boolean storesMixedCaseIds) {
        this.storesMixedCaseIds = storesMixedCaseIds;
        if (storesMixedCaseIds){
            this.storesLowerCaseIds = false;
            this.storesUpperCaseIds = false;
        }
    }

    public Boolean getStoresUpperCaseIds() {
        return storesUpperCaseIds;
    }

    void setStoresUpperCaseIds(Boolean storesUpperCaseIds) {
        this.storesUpperCaseIds = storesUpperCaseIds;
        if (storesUpperCaseIds){
            this.storesLowerCaseIds = false;
            this.storesMixedCaseIds = false;
        }
    }
    /**
     * This method does not do anything anymore.
     * It returns the same string passed into the parameter.
     * 
     * @param identifier
     * @return 
     */
    String convertId (String identifier){
  
        return identifier;
//        if (storesLowerCaseIds){
//            return identifier.toLowerCase();
//        }
//        else if (storesUpperCaseIds){
//            return identifier.toUpperCase();
//        }
//        else {
//            return identifier;
//        }
    }
    
}
