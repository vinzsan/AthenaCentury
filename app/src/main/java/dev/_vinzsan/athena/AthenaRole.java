package dev._vinzsan.athena;

public class AthenaRole {
    public enum PickRolesTypes {
        ADVENTURE("Advemture"),
        MAGE("Mage"),
        KNIGHT("Knight"),
        ARCHERY("Archery");

        public final String label;

        PickRolesTypes(String label){
            this.label = label;
        }
    }

    /* NOTE : Istana,Bandit,Merchant,Thief,Assassin,Spy */
    public enum PickSubRolesTypes {
        AULICUS("Istana"),
        LATRO("Bandit"),
        MERCATOR("Merchant"),
        FUR("Thief"),
        SICARIUS("Assassin"),
        SPECULATOR("Spy");
        
        public final String label;

        PickSubRolesTypes(String label){
            this.label = label;
        }
    }
}
