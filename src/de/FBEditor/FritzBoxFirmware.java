package de.FBEditor;


/**
 * Class modelling firmware and box information
 * 
 */
public class FritzBoxFirmware {

    private String FritzboxName;
    private int boxtype; // Muss Integer sein wegen neuen Firmware Typ > 127
    private byte majorFirmwareVersion;
    private byte minorFirmwareVersion;
    private String modFirmwareVersion;
    @SuppressWarnings("unused")
	private String language;

    /**
     * Firmware Constructor using Strings
     * 
     * @param FritzboxName
     * @param boxtype
     * @param majorFirmwareVersion
     * @param minorFirmwareVersion
     * @param modFirmwareVersion
     * @param language
     */
    public FritzBoxFirmware(String FritzboxName, String boxtype,
            String majorFirmwareVersion, String minorFirmwareVersion,
            String modFirmwareVersion, String language) {
        this.FritzboxName = FritzboxName;
        this.boxtype = Integer.parseInt(boxtype);
        this.majorFirmwareVersion = Byte.parseByte(majorFirmwareVersion);
        this.minorFirmwareVersion = Byte.parseByte(minorFirmwareVersion);
        this.modFirmwareVersion = modFirmwareVersion;
        this.language = language;
    }

    public String getFritzboxName() {
        return FritzboxName;
    }

    /**
     * @return Returns the boxtype.
     */
    public final int getBoxType() {
        return boxtype;
    }

    /**
     * @return Returns the majorFirmwareVersion.
     */
    public final byte getMajorFirmwareVersion() {
        return majorFirmwareVersion;
    }

    /**
     * @return Returns the minorFirmwareVersion.
     */
    public final byte getMinorFirmwareVersion() {
        return minorFirmwareVersion;
    }

    /**
     * @return Returns the minorFirmwareVersion.
     */
    public final String getModFirmwareVersion() {
        return modFirmwareVersion;
    }

    /**
     * @return Returns the majorFirmwareVersion.
     */
    public final String getFirmwareVersion() {
        String boxtypeStr = Integer.toString(boxtype);
        String majorStr = Byte.toString(majorFirmwareVersion);
        String minorStr = Byte.toString(minorFirmwareVersion);
        if (boxtypeStr.length() == 1) {
            boxtypeStr = "0" + boxtypeStr;
        }
        if (majorStr.length() == 1) {
            majorStr = "0" + majorStr;
        }
        if (minorStr.length() == 1) {
            minorStr = "0" + minorStr;
        }
        return boxtypeStr + "." + majorStr + "." + minorStr; // +
                                                                // modFirmwareVersion;
    }

    public String getBoxName() {

 	      System.out.println( "Debug FritzBoxFirmware: " + FritzboxName + " -> " + boxtype );
 	    
          if ((FritzboxName.length() > 0) && (boxtype > 0)) {
           return FritzboxName;
          } else {
           switch (boxtype) {
            case 5:
             return "FRITZ!Box";
            case 6:
             return "FRITZ!Box Fon";
            case 8:
             return "FRITZ!Box Fon WLAN";
            case 9:
             return "Fritz!Box SL WLAN";
            case 10:
             return "Fritz!Box SL";
            case 11:
             return "FRITZ!Box ata";
            case 12:
             return "FRITZ!Box 5050";
            case 13:
             return "FRITZ!Box Fon Mini VoIP";
            case 14:
             return "FRITZ!Box 7050";
            case 15:
             return "Eumex 300 IP";
            case 16:
             return "FRITZ!Box 3050";
            case 17:
             return "FRITZ!Box 2030";
            case 19:
             return "FRITZ!Box 3070";
            case 20:
             return "FRITZ!Box 2070";
            case 21:
             return "FRITZ!Box 3030";
            case 23:
             return "FRITZ!Box 5010";
            case 25:
             return "FRITZ!Box 5012";
            case 27:
             return "T-Com Sinus W 500V";
            case 28:
             return "Speedport W501V";
            case 29:
             return "FRITZ!Box 7170";
            case 30:
             return "FRITZ!Box 7140";
            case 33:
             return "Speedport W701V";
            case 34:
             return "Speedport W900V";
            case 36:
             return "FRITZ!Box VoIP Gateway 5188";
            case 38:
             return "FRITZ!Box 7150";
            case 39:
             return "FRITZ!Box Fon WLAN 7140 Annex A";
            case 40:
             return "FRITZ!Box 7141";
            case 43:
             return "FRITZ!Box 5140";
            case 44:
             return "FRITZ!Box 3130";
            case 45:
             return "FRITZ!Box 2031";
            case 48:
             return "FRITZ!Box Fon 5010 Annex A";
            case 49:
             return "FRITZ!Box 3170";
            case 50:
             return "FRITZ!Box 3131";
            case 51:
             return "FRITZ!Box 2170";
            case 54:
             return "FRITZ!Box 7270";
            case 55:
             return "FRITZ!Media 8020/8040";
            case 56:
             return "FRITZ!Box Fon 5124";
            case 57:
             return "FRITZ!Box Fon 5124 Annex A";
            case 58:
             return "FRITZ!Box 7170 (CH-AT-Edition)";
            case 60:
             return "FRITZ!Box 7113";
            case 62:
             return "Alice IAD 5130";
            case 63:
             return "FRITZ!Box 2110";
            case 64:
             return "Speedport W721V";
            case 65:
             return "Speedport W920V";
            case 66:
             return "Speedport W503V Typ A (congstar komplett Box)";
            case 67:
             return "FRITZ!Box 3270";
            case 68:
             return "FRITZ!WLAN Repeater N/G";
            case 73:
             return "FRITZ!Box 7240";
            case 74:
             return "FRITZ!Box 7270v3";
            case 75:
             return "FRITZ!Box 7570";
            case 80:
             return "Speedport W722V Typ A";
            case 81:
             return "FRITZ!Box 7570 HN";
            case 83:
             return "FRITZ!Box Fon 5113 Annex A";
            case 84:
             return "FRITZ!Box 7390";
            case 85:
             return "FRITZ!Box 6360 Cable";
            case 87:
             return "FRITZ!Box 7112";
            case 90:
             return "FRITZ!Box Fon WLAN 7113 Annex A";
            case 91:
             return "FRITZ!Media 8260";
            case 93:
             return "Speedport W501V";
            case 96:
             return "FRITZ!Box 3270 v3";
            case 99:
             return "FRITZ!Box 7340";
            case 100:
             return "FRITZ!Box 7320";
            case 101:
             return "FRITZ!Box Fon WLAN Speedport W701V (FRITZ!WLAN Repeater 300E)";
            case 102:
             return "FRITZ!Box Fon WLAN Speedport W900V (IAD WLAN 3331)";
            case 103:
             return "FRITZ!Box 3370";
            case 104:
             return "FRITZ!Box 6320 Cable";
            case 105:
             return "FRITZ!Box 6840 LTE";
            case 107:
             return "FRITZ!Box 7330";
            case 108:
             return "FRITZ!Box 6810 LTE";
            case 109:
             return "FRITZ!Box 7360 SL";
            case 110:
             return "FRITZ!Box 6320 v2 Cable";
            case 111:
             return "FRITZ!Box 7360";
            case 113:
             return "FRITZ!Box 7490";
            case 115:
             return "FRITZ!Box 6340 Cable";
            case 116:
             return "FRITZ!Box 7330 SL";
            case 117:
             return "FRITZ!Box 7312";
            case 118:
             return "FRITZ!Powerline 546E";
            case 120:
             return "FRITZ!Box 7272";
            case 121:
             return "FRITZ!Box 3390";
            case 122:
             return "FRITZ!WLAN Repeater 310";
            case 123:
             return "FRITZ!Box 6842 LTE";
            case 124:
             return "FRITZ!Box 7360";
            case 125:
             return "FRITZ!Box 3270 v3 (IT-Edition)";
            case 126:
             return "FRITZ!Box 3272";
            case 131:
             return "FRITZ!Box 7362 SL";
            case 133:
             return "FRITZ!WLAN Repeater DVB-C";
            case 137:
             return "FRITZ!Box 7412";
            case 140:
             return "FRITZ!Box 3490";
            case 141:
             return "FRITZ!Box 6490 Cable";
            case 146:
             return "FRITZ!Box 7430";
            case 148:
             return "FRITZ!Box 6590 Cable";
            case 149:
             return "FRITZ!Box 7560";
            case 152:
             return "FRITZ!Box 7581 (International)";
            case 153:
             return "FRITZ!Box 7580";
            case 154:
             return "FRITZ!Box 7590";
            case 162:
             return "FRITZ!Box 6890 LTE";
            default:
             return "unknown";
           }
          }
    }
}
