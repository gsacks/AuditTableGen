/*    Copyright 2014 Certifi Inc.
 *
 *    This file is part of AuditTableGen.
 *
 *        AuditTableGen is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        (at your option) any later version.
 *
 *        AuditTableGen is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with AuditTableGen.  If not, see <http://www.gnu.org/licenses/>.
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
