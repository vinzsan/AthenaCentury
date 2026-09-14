package dev._vinzsan.athena;

import dev._vinzsan.athena.AthenaRole.PickRolesTypes;
import dev._vinzsan.athena.AthenaRole.PickSubRolesTypes;

public class AthenaRolesPick {
    public final PickRolesTypes roles;
    public final PickSubRolesTypes subRoles;
    public final boolean isAlreadyRoled;

    public AthenaRolesPick(PickRolesTypes role,PickSubRolesTypes subRoles,boolean alreadyRole){
        this.roles = role;
        this.subRoles = subRoles;
        this.isAlreadyRoled = alreadyRole;
    }

    public boolean isAlreadyGetRole(){
        return isAlreadyRoled ? true : false;
    }
    
    public PickSubRolesTypes getSubRoles(){
        return this.subRoles;
    }
    
    public String getSubRolesName(){
        return this.subRoles.label != null ? this.subRoles.label : "(Role belum di pilih)";
    }
}
