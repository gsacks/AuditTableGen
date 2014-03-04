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
public enum ConfigAttributeTypes {
    
    exclude,
    include,
    tableprefix,
    tablepostfix,
    columnprefix,
    columnpostfix,
    auditinsert,
    auditupdate,
    auditdelete,
    iddatatype,
    userdatatype,
    actiondatatype,
    timestampdatatype,
    sessionusersql,
    sessionuserdatatype,
    sessionuserdatasize,
    unknown;
    
}
