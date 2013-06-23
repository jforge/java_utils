package com.mplify.countries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mplify.checkers.Check;
import com.mplify.enums.EnumerativeTypeUsingInteger;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that is used to represent countries. Formerly a database table was
 * used but that does not seems to be necessary and gives trouble when testing
 * or setting up in small programs (some leftover from the ICMS days). Also, 
 * using a CountryId instead of an 'int' is a *extreme* improvement because
 * static type checking rocks!
 * 
 * Country code ISO codes can be found here:
 * 
 * http://transpatent.com/landverz.html
 * 
 * 2005.01.28 - Created from database table. The ide is to have better typed
 *              values than just a 'int'
 * 2005.01.31 - Static constructor bettered.
 * 2005.04.26 - Reviewed, added _NOWHERE
 * 2005.04.29 - Added WESTERN SAHARA (nominally under control of Morocco)
 *              and the MAP_BY_ISOCODE map.
 * 2009.01.07 - Added INMARSAT/IRIDIUM numbers, added canonical names,
 *              somewhat reviewed
 * 2010.12.14 - Added the iNum prefix for the "iNum Network" (http://www.inum.net/)          
 * 2011.06.24 - Bizzaredly, it turns out the private iNum code (232) is
 *              already used, so the class won't initialize!! As this is
 *              not a problem at runtime, this class cannot be used much.
 *              Changing to 242 anyway.
 * 2011.10.19 - Adapted for LOG4J --> SLF4J migration
 ******************************************************************************/

public class CountryId implements EnumerativeTypeUsingInteger {

//    private final static String CLASS = CountryId.class.getName();
//    private final static Logger LOGGER_obtain = LoggerFactory.getLogger(CLASS + ".obtain");

    /*
     * Members.
     */

    private final int value; // actual value -- used in the database, thus > 0, except for UNDEFINED 
    private final String name; // human-readable text, not very beautiful, output only, never null
    private final String canoName; // canonical name, can be used to look up a code by name, never null

    /*
     * CountryId may not actually be a country (we are cheating on Object-Orientation here) It may be a "Network" or a
     * "Country". This label says what it is. We could add "Region" or "Zone" but that does not seem useful for now.
     */

    public enum Type {
        Network, Country, Dummy
    }

    private final Type type;

    /*
     * If this is a country, also store the 2-letter ISO-code. Null if not a country
     */

    private final String isoCode;

    /*
     * These are the possible instance of this class; they form a quasi-enumeration These are public and used by client
     * code. _UNDEFINED: used in avoiding (null) values and also in providing an _UNDEFINED value to the user _NOWHERE:
     * used in giving a CountryId to timezones that don't have one (the canonical UTC timezones)
     */

    public final static CountryId _UNDEFINED; // used in avoiding (null) values; not a good idea maybe?
    public final static CountryId AFGHANISTAN;
    public final static CountryId ALBANIA;
    public final static CountryId ALGERIA;
    public final static CountryId AMERICAN_SAMOA;
    public final static CountryId ANDORRA;
    public final static CountryId ANGOLA;
    public final static CountryId ANGUILLA;
    public final static CountryId ANTARCTICA;
    public final static CountryId ANTIGUA_AND_BARBUDA;
    public final static CountryId ARGENTINA;
    public final static CountryId ARMENIA;
    public final static CountryId ARUBA;
    public final static CountryId AUSTRALIA;
    public final static CountryId AUSTRIA;
    public final static CountryId AZERBAIJAN;
    public final static CountryId BAHAMAS;
    public final static CountryId BAHRAIN;
    public final static CountryId BANGLADESH;
    public final static CountryId BARBADOS;
    public final static CountryId BELARUS;
    public final static CountryId BELGIUM;
    public final static CountryId BELIZE;
    public final static CountryId BENIN;
    public final static CountryId BERMUDA;
    public final static CountryId BHUTAN;
    public final static CountryId BOLIVIA;
    public final static CountryId BOSNIA_AND_HERZEGOVINA;
    public final static CountryId BOTSWANA;
    public final static CountryId BRAZIL;
    public final static CountryId BRUNEI_DARUSSALAM;
    public final static CountryId BULGARIA;
    public final static CountryId BURKINA_FASO;
    public final static CountryId BURUNDI;
    public final static CountryId CAMBODIA;
    public final static CountryId CAMEROON;
    public final static CountryId CANADA;
    public final static CountryId CAPE_VERDE;
    public final static CountryId CAYMAN_ISLANDS;
    public final static CountryId CENTRAL_AFRICAN_REPUBLIC;
    public final static CountryId CHAD;
    public final static CountryId CHILE;
    public final static CountryId CHINA;
    public final static CountryId CHRISTMAS_ISLAND;
    public final static CountryId COCOS_KEELING_ISLANDS;
    public final static CountryId COLOMBIA;
    public final static CountryId COMOROS;
    public final static CountryId CONGO;
    public final static CountryId CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE;
    public final static CountryId COOK_ISLANDS;
    public final static CountryId COSTA_RICA;
    public final static CountryId COTE_DIVOIRE;
    public final static CountryId CROATIA;
    public final static CountryId CUBA;
    public final static CountryId CYPRUS;
    public final static CountryId CZECH_REPUBLIC;
    public final static CountryId DENMARK;
    public final static CountryId DJIBOUTI;
    public final static CountryId DOMINICA;
    public final static CountryId DOMINICAN_REPUBLIC;
    public final static CountryId EAST_TIMOR;
    public final static CountryId ECUADOR;
    public final static CountryId EGYPT;
    public final static CountryId EL_SALVADOR;
    public final static CountryId EQUATORIAL_GUINEA;
    public final static CountryId ERITREA;
    public final static CountryId ESTONIA;
    public final static CountryId ETHIOPIA;
    public final static CountryId FALKLAND_ISLANDS_MALVINAS;
    public final static CountryId FAROE_ISLANDS;
    public final static CountryId FIJI;
    public final static CountryId FINLAND;
    public final static CountryId FRANCE;
    public final static CountryId FRENCH_GUIANA;
    public final static CountryId FRENCH_POLYNESIA;
    public final static CountryId FRENCH_SOUTHERN_TERRITORIES;
    public final static CountryId GABON;
    public final static CountryId GAMBIA;
    public final static CountryId GEORGIA;
    public final static CountryId GERMANY;
    public final static CountryId GHANA;
    public final static CountryId GIBRALTAR;
    public final static CountryId GREECE;
    public final static CountryId GREENLAND;
    public final static CountryId GRENADA;
    public final static CountryId GUADELOUPE;
    public final static CountryId GUAM;
    public final static CountryId GUATEMALA;
    public final static CountryId GUINEA;
    public final static CountryId GUINEA_BISSAU;
    public final static CountryId GUYANA;
    public final static CountryId HAITI;
    public final static CountryId HOLY_SEE_VATICAN_CITY_STATE;
    public final static CountryId HONDURAS;
    public final static CountryId HONG_KONG;
    public final static CountryId HUNGARY;
    public final static CountryId ICELAND;
    public final static CountryId INDIA;
    public final static CountryId INDONESIA;
    public final static CountryId IRAN_ISLAMIC_REPUBLIC_OF;
    public final static CountryId IRAQ;
    public final static CountryId IRELAND;
    public final static CountryId ISRAEL;
    public final static CountryId ITALY;
    public final static CountryId JAMAICA;
    public final static CountryId JAPAN;
    public final static CountryId JORDAN;
    public final static CountryId KAZAKSTAN;
    public final static CountryId KENYA;
    public final static CountryId KIRIBATI;
    public final static CountryId KOREA_DEMOCRATIC_PEOPLES_REPUBLIC_OF;
    public final static CountryId KOREA_REPUBLIC_OF;
    public final static CountryId KUWAIT;
    public final static CountryId KYRGYZSTAN;
    public final static CountryId LAO_PEOPLES_DEMOCRATIC_REPUBLIC;
    public final static CountryId LATVIA;
    public final static CountryId LEBANON;
    public final static CountryId LESOTHO;
    public final static CountryId LIBERIA;
    public final static CountryId LIBYAN_ARAB_JAMAHIRIYA;
    public final static CountryId LIECHTENSTEIN;
    public final static CountryId LITHUANIA;
    public final static CountryId LUXEMBOURG;
    public final static CountryId MACAU;
    public final static CountryId MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF;
    public final static CountryId MADAGASCAR;
    public final static CountryId MALAWI;
    public final static CountryId MALAYSIA;
    public final static CountryId MALDIVES;
    public final static CountryId MALI;
    public final static CountryId MALTA;
    public final static CountryId MARSHALL_ISLANDS;
    public final static CountryId MARTINIQUE;
    public final static CountryId MAURITANIA;
    public final static CountryId MAURITIUS;
    public final static CountryId MAYOTTE;
    public final static CountryId MEXICO;
    public final static CountryId MICRONESIA_FEDERATED_STATES_OF;
    public final static CountryId MOLDOVA_REPUBLIC_OF;
    public final static CountryId MONACO;
    public final static CountryId MONGOLIA;
    public final static CountryId MONTSERRAT;
    public final static CountryId MOROCCO;
    public final static CountryId MOZAMBIQUE;
    public final static CountryId MYANMAR;
    public final static CountryId NAMIBIA;
    public final static CountryId NAURU;
    public final static CountryId NEPAL;
    public final static CountryId NETHERLANDS;
    public final static CountryId NETHERLANDS_ANTILLES;
    public final static CountryId NEW_CALEDONIA;
    public final static CountryId NEW_ZEALAND;
    public final static CountryId NICARAGUA;
    public final static CountryId NIGER;
    public final static CountryId NIGERIA;
    public final static CountryId NIUE;
    public final static CountryId NORFOLK_ISLAND;
    public final static CountryId NORTHERN_MARIANA_ISLANDS;
    public final static CountryId NORWAY;
    public final static CountryId OMAN;
    public final static CountryId PAKISTAN;
    public final static CountryId PALAU;
    public final static CountryId PALESTINIAN_TERRITORY_OCCUPIED;
    public final static CountryId PANAMA;
    public final static CountryId PAPUA_NEW_GUINEA;
    public final static CountryId PARAGUAY;
    public final static CountryId PERU;
    public final static CountryId PHILIPPINES;
    public final static CountryId POLAND;
    public final static CountryId PORTUGAL;
    public final static CountryId PUERTO_RICO;
    public final static CountryId QATAR;
    public final static CountryId REUNION;
    public final static CountryId ROMANIA;
    public final static CountryId RUSSIAN_FEDERATION;
    public final static CountryId RWANDA;
    public final static CountryId SAINT_HELENA;
    public final static CountryId SAINT_KITTS_AND_NEVIS;
    public final static CountryId SAINT_LUCIA;
    public final static CountryId SAINT_PIERRE_AND_MIQUELON;
    public final static CountryId SAINT_VINCENT_AND_THE_GRENADINES;
    public final static CountryId SAMOA;
    public final static CountryId SAN_MARINO;
    public final static CountryId SAO_TOME_AND_PRINCIPE;
    public final static CountryId SAUDI_ARABIA;
    public final static CountryId SENEGAL;
    public final static CountryId SEYCHELLES;
    public final static CountryId SIERRA_LEONE;
    public final static CountryId SINGAPORE;
    public final static CountryId SLOVAKIA;
    public final static CountryId SLOVENIA;
    public final static CountryId SOLOMON_ISLANDS;
    public final static CountryId SOMALIA;
    public final static CountryId SOUTH_AFRICA;
    public final static CountryId SPAIN;
    public final static CountryId SRI_LANKA;
    public final static CountryId SUDAN;
    public final static CountryId SURINAME;
    public final static CountryId SWAZILAND;
    public final static CountryId SWEDEN;
    public final static CountryId SWITZERLAND;
    public final static CountryId SYRIAN_ARAB_REPUBLIC;
    public final static CountryId TAIWAN_PROVINCE_OF_CHINA;
    public final static CountryId TAJIKISTAN;
    public final static CountryId TANZANIA_UNITED_REPUBLIC_OF;
    public final static CountryId THAILAND;
    public final static CountryId TOGO;
    public final static CountryId TOKELAU;
    public final static CountryId TONGA;
    public final static CountryId TRINIDAD_AND_TOBAGO;
    public final static CountryId TUNISIA;
    public final static CountryId TURKEY;
    public final static CountryId TURKMENISTAN;
    public final static CountryId TURKS_AND_CAICOS_ISLANDS;
    public final static CountryId TUVALU;
    public final static CountryId UGANDA;
    public final static CountryId UKRAINE;
    public final static CountryId UNITED_ARAB_EMIRATES;
    public final static CountryId UNITED_KINGDOM;
    public final static CountryId UNITED_STATES;
    public final static CountryId URUGUAY;
    public final static CountryId UZBEKISTAN;
    public final static CountryId VANUATU;
    public final static CountryId VENEZUELA;
    public final static CountryId VIET_NAM;
    public final static CountryId VIRGIN_ISLANDS_BRITISH;
    public final static CountryId VIRGIN_ISLANDS_U_S;
    public final static CountryId WALLIS_AND_FUTUNA;
    public final static CountryId YEMEN;
    public final static CountryId YUGOSLAVIA;
    public final static CountryId ZAMBIA;
    public final static CountryId ZIMBABWE;
    public final static CountryId WESTERN_SAHARA;
    public final static CountryId ICO_GLOBAL; // Satellite System
    public final static CountryId ELLIPSO; // Satellite System
    public final static CountryId IRIDIUM; // Satellite System
    public final static CountryId GLOBALSTAR; // Satellite System
    public final static CountryId INMARSAT_ATLANTIC_OCEAN_REGION_EAST; // Satellite System
    public final static CountryId INMARSAT_PACIFIC_OCEAN_REGION; // Satellite System
    public final static CountryId INMARSAT_INDIAN_OCEAN_REGION; // Satellite System
    public final static CountryId INMARSAT_ATLANTIC_OCEAN_REGION_WEST; // Satellite System
    public final static CountryId INMARSAT_SINGLE_NETWORK_ACCESS_CODE; // Satellite System
    public final static CountryId INUM_NUMBER; // New ITU iNum number (http://www.inum.net/)
    public final static CountryId _NOWHERE; // used in giving a CountryId to UTC timezones

    /*
     * A list of the values. The first two are the countries useful for the user, i.e. the "_NOWHERE" CountryId is not
     * in these lists. One list contains the "_UNDEFINED" country, one does not. GUI code may choose one or the other
     * list. The third contains both "_UNDEFINED" and "_NOWHERE"
     */

    public final static List<CountryId> LIST_WITH_UNDEFINED;
    public final static List<CountryId> LIST_WITHOUT_UNDEFINED;
    public final static List<CountryId> LIST_WITH_UNDEFINED_AND_NOWHERE;

    /*
     * The values hashed by their Integer id, private. Also contains _UNDEFINED and _NOWHERE.
     */

    private final static Map<Integer, CountryId> MAP_BY_VALUE;

    /*
     * The values hashed by their ISO code, currently private. Only contains countries. ISO codes are uppercase always.
     */

    private final static Map<String, CountryId> MAP_BY_ISOCODE;

    /*
     * The values hashed by their Canonical Name, currently private. Does not contain NOWHERE nor UNDEFINED
     */

    private final static Map<String, CountryId> MAP_BY_CANONICAL_NAME;

    /*
     * Static construction takes care to assign unique (and constant) numeric identifiers to the various instances. They
     * must be stable because they are used in the database. At the same time, the values are assigned to the 'list',
     * which will then be made immutable and assigned to LIST.
     */

    static {

        List<CountryId> list = new ArrayList<CountryId>();

        list.add(AFGHANISTAN = new CountryId(1, "Afghanistan", Type.Country, "AFGHANISTAN", "AF"));
        list.add(ALBANIA = new CountryId(2, "Albania", Type.Country, "ALBANIA", "AL"));
        list.add(ALGERIA = new CountryId(3, "Algeria", Type.Country, "ALGERIA", "DZ"));
        list.add(AMERICAN_SAMOA = new CountryId(4, "American Samoa", Type.Country, "AMERICAN_SAMOA", "AS"));
        list.add(ANDORRA = new CountryId(5, "Andorra", Type.Country, "ANDORRA", "AD"));
        list.add(ANGOLA = new CountryId(6, "Angola", Type.Country, "ANGOLA", "AO"));
        list.add(ANGUILLA = new CountryId(7, "Anguilla", Type.Country, "ANGUILLA", "AI"));
        list.add(ANTARCTICA = new CountryId(8, "Antarctica", Type.Country, "ANTARCTICA", "AQ"));
        list.add(ANTIGUA_AND_BARBUDA = new CountryId(9, "Antigua and Barbuda", Type.Country, "ANTIGUA_AND_BARBUDA", "AG"));
        list.add(ARGENTINA = new CountryId(10, "Argentina", Type.Country, "ARGENTINA", "AR"));
        list.add(ARMENIA = new CountryId(11, "Armenia", Type.Country, "ARMENIA", "AM"));
        list.add(ARUBA = new CountryId(12, "Aruba", Type.Country, "ARUBA", "AW"));
        list.add(AUSTRALIA = new CountryId(13, "Australia", Type.Country, "AUSTRALIA", "AU"));
        list.add(AUSTRIA = new CountryId(14, "Austria", Type.Country, "AUSTRIA", "AT"));
        list.add(AZERBAIJAN = new CountryId(15, "Azerbaijan", Type.Country, "AZERBAIJAN", "AZ"));
        list.add(BAHAMAS = new CountryId(16, "Bahamas", Type.Country, "BAHAMAS", "BS"));
        list.add(BAHRAIN = new CountryId(17, "Bahrain", Type.Country, "BAHRAIN", "BH"));
        list.add(BANGLADESH = new CountryId(18, "Bangladesh", Type.Country, "BANGLADESH", "BD"));
        list.add(BARBADOS = new CountryId(19, "Barbados", Type.Country, "BARBADOS", "BB"));
        list.add(BELARUS = new CountryId(20, "Belarus", Type.Country, "BELARUS", "BY"));
        list.add(BELGIUM = new CountryId(21, "Belgium", Type.Country, "BELGIUM", "BE"));
        list.add(BELIZE = new CountryId(22, "Belize", Type.Country, "BELIZE", "BZ"));
        list.add(BENIN = new CountryId(23, "Benin", Type.Country, "BENIN", "BJ"));
        list.add(BERMUDA = new CountryId(24, "Bermuda", Type.Country, "BERMUDA", "BM"));
        list.add(BHUTAN = new CountryId(25, "Bhutan", Type.Country, "BHUTAN", "BT"));
        list.add(BOLIVIA = new CountryId(26, "Bolivia", Type.Country, "BOLIVIA", "BO"));
        list.add(BOSNIA_AND_HERZEGOVINA = new CountryId(27, "Bosnia and Herzegovina", Type.Country, "BOSNIA_AND_HERZEGOVINA", "BA"));
        list.add(BOTSWANA = new CountryId(28, "Botswana", Type.Country, "BOTSWANA", "BW"));
        list.add(BRAZIL = new CountryId(29, "Brazil", Type.Country, "BRAZIL", "BR"));
        list.add(BRUNEI_DARUSSALAM = new CountryId(30, "Brunei Darussalam", Type.Country, "BRUNEI_DARUSSALAM", "BN"));
        list.add(BULGARIA = new CountryId(31, "Bulgaria", Type.Country, "BULGARIA", "BG"));
        list.add(BURKINA_FASO = new CountryId(32, "Burkina Faso", Type.Country, "BURKINA_FASO", "BF"));
        list.add(BURUNDI = new CountryId(33, "Burundi", Type.Country, "BURUNDI", "BI"));
        list.add(CAMBODIA = new CountryId(34, "Cambodia", Type.Country, "CAMBODIA", "KH"));
        list.add(CAMEROON = new CountryId(35, "Cameroon", Type.Country, "CAMEROON", "CM"));
        list.add(CANADA = new CountryId(36, "Canada", Type.Country, "CANADA", "CA"));
        list.add(CAPE_VERDE = new CountryId(37, "Cape Verde", Type.Country, "CAPE_VERDE", "CV"));
        list.add(CAYMAN_ISLANDS = new CountryId(38, "Cayman Islands", Type.Country, "CAYMAN_ISLANDS", "KY"));
        list.add(CENTRAL_AFRICAN_REPUBLIC = new CountryId(39, "Central African Republic", Type.Country, "CENTRAL_AFRICAN_REPUBLIC", "CF"));
        list.add(CHAD = new CountryId(40, "Chad", Type.Country, "CHAD", "TD"));
        list.add(CHILE = new CountryId(41, "Chile", Type.Country, "CHILE", "CL"));
        list.add(CHINA = new CountryId(42, "China", Type.Country, "CHINA", "CN"));
        list.add(CHRISTMAS_ISLAND = new CountryId(43, "Christmas island", Type.Country, "CHRISTMAS_ISLAND", "CX"));
        list.add(COCOS_KEELING_ISLANDS = new CountryId(44, "Cocos (Keeling) Islands", Type.Country, "COCOS_KEELING_ISLANDS", "CC"));
        list.add(COLOMBIA = new CountryId(45, "Colombia", Type.Country, "COLOMBIA", "CO"));
        list.add(COMOROS = new CountryId(46, "Comoros", Type.Country, "COMOROS", "KM"));
        list.add(CONGO = new CountryId(47, "Congo", Type.Country, "CONGO", "CG"));
        list.add(CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE = new CountryId(48, "Congo, the Democratic Republic of the", Type.Country, "CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE", "CD"));
        list.add(COOK_ISLANDS = new CountryId(49, "Cook Islands", Type.Country, "COOK_ISLANDS", "CK"));
        list.add(COSTA_RICA = new CountryId(50, "Costa Rica", Type.Country, "COSTA_RICA", "CR"));
        list.add(CROATIA = new CountryId(52, "Croatia", Type.Country, "CROATIA", "HR"));
        list.add(CUBA = new CountryId(53, "Cuba", Type.Country, "CUBA", "CU"));
        list.add(CYPRUS = new CountryId(54, "Cyprus", Type.Country, "CYPRUS", "CY"));
        list.add(CZECH_REPUBLIC = new CountryId(55, "Czech Republic", Type.Country, "CZECH_REPUBLIC", "CZ"));
        list.add(COTE_DIVOIRE = new CountryId(51, "Côte d'Ivoire", Type.Country, "COTE_DIVOIRE", "CI"));
        list.add(DENMARK = new CountryId(56, "Denmark", Type.Country, "DENMARK", "DK"));
        list.add(DJIBOUTI = new CountryId(57, "Djibouti", Type.Country, "DJIBOUTI", "DJ"));
        list.add(DOMINICA = new CountryId(58, "Dominica", Type.Country, "DOMINICA", "DM"));
        list.add(DOMINICAN_REPUBLIC = new CountryId(59, "Dominican Republic", Type.Country, "DOMINICAN_REPUBLIC", "DO"));
        list.add(EAST_TIMOR = new CountryId(60, "East Timor", Type.Country, "EAST_TIMOR", "TP"));
        list.add(ECUADOR = new CountryId(61, "Ecuador", Type.Country, "ECUADOR", "EC"));
        list.add(EGYPT = new CountryId(62, "Egypt", Type.Country, "EGYPT", "EG"));
        list.add(EL_SALVADOR = new CountryId(63, "El Salvador", Type.Country, "EL_SALVADOR", "SV"));
        list.add(ELLIPSO = new CountryId(234, "Ellipso", Type.Network, "ELLIPSO", ""));
        list.add(EQUATORIAL_GUINEA = new CountryId(64, "Equatorial Guinea", Type.Country, "EQUATORIAL_GUINEA", "GQ"));
        list.add(ERITREA = new CountryId(65, "Eritrea", Type.Country, "ERITREA", "ER"));
        list.add(ESTONIA = new CountryId(66, "Estonia", Type.Country, "ESTONIA", "EE"));
        list.add(ETHIOPIA = new CountryId(67, "Ethiopia", Type.Country, "ETHIOPIA", "ET"));
        list.add(FALKLAND_ISLANDS_MALVINAS = new CountryId(68, "Falkland Islands (Malvinas)", Type.Country, "FALKLAND_ISLANDS_MALVINAS", "FK"));
        list.add(FAROE_ISLANDS = new CountryId(69, "Faroe Islands", Type.Country, "FAROE_ISLANDS", "FO"));
        list.add(FIJI = new CountryId(70, "Fiji", Type.Country, "FIJI", "FJ"));
        list.add(FINLAND = new CountryId(71, "Finland", Type.Country, "FINLAND", "FI"));
        list.add(FRANCE = new CountryId(72, "France", Type.Country, "FRANCE", "FR"));
        list.add(FRENCH_GUIANA = new CountryId(73, "French Guiana", Type.Country, "FRENCH_GUIANA", "GF"));
        list.add(FRENCH_POLYNESIA = new CountryId(74, "French Polynesia", Type.Country, "FRENCH_POLYNESIA", "PF"));
        list.add(FRENCH_SOUTHERN_TERRITORIES = new CountryId(75, "French Southern Territories", Type.Country, "FRENCH_SOUTHERN_TERRITORIES", "TF"));
        list.add(GABON = new CountryId(76, "Gabon", Type.Country, "GABON", "GA"));
        list.add(GAMBIA = new CountryId(77, "Gambia", Type.Country, "GAMBIA", "GM"));
        list.add(GEORGIA = new CountryId(78, "Georgia", Type.Country, "GEORGIA", "GE"));
        list.add(GERMANY = new CountryId(79, "Germany", Type.Country, "GERMANY", "DE"));
        list.add(GHANA = new CountryId(80, "Ghana", Type.Country, "GHANA", "GH"));
        list.add(GIBRALTAR = new CountryId(81, "Gibraltar", Type.Country, "GIBRALTAR", "GI"));
        list.add(GLOBALSTAR = new CountryId(236, "Globalstar", Type.Network, "GLOBALSTAR", ""));
        list.add(GREECE = new CountryId(82, "Greece", Type.Country, "GREECE", "GR"));
        list.add(GREENLAND = new CountryId(83, "Greenland", Type.Country, "GREENLAND", "GL"));
        list.add(GRENADA = new CountryId(84, "Grenada", Type.Country, "GRENADA", "GD"));
        list.add(GUADELOUPE = new CountryId(85, "Guadeloupe", Type.Country, "GUADELOUPE", "GP"));
        list.add(GUAM = new CountryId(86, "Guam", Type.Country, "GUAM", "GU"));
        list.add(GUATEMALA = new CountryId(87, "Guatemala", Type.Country, "GUATEMALA", "GT"));
        list.add(GUINEA = new CountryId(88, "Guinea", Type.Country, "GUINEA", "GN"));
        list.add(GUINEA_BISSAU = new CountryId(89, "Guinea-Bissau", Type.Country, "GUINEA_BISSAU", "GW"));
        list.add(GUYANA = new CountryId(90, "Guyana", Type.Country, "GUYANA", "GY"));
        list.add(HAITI = new CountryId(91, "Haiti", Type.Country, "HAITI", "HT"));
        list.add(HOLY_SEE_VATICAN_CITY_STATE = new CountryId(92, "Holy See (Vatican City State)", Type.Country, "HOLY_SEE_VATICAN_CITY_STATE", "VA"));
        list.add(HONDURAS = new CountryId(93, "Honduras", Type.Country, "HONDURAS", "HN"));
        list.add(HONG_KONG = new CountryId(94, "Hong Kong", Type.Country, "HONG_KONG", "HK"));
        list.add(HUNGARY = new CountryId(95, "Hungary", Type.Country, "HUNGARY", "HU"));
        list.add(ICO_GLOBAL = new CountryId(233, "ICO Global", Type.Network, "ICO_GLOBAL", ""));
        list.add(INMARSAT_ATLANTIC_OCEAN_REGION_EAST = new CountryId(237, "INMARSAT AOR-E", Type.Network, "INMARSAT_ATLANTIC_OCEAN_REGION_EAST", ""));
        list.add(INMARSAT_ATLANTIC_OCEAN_REGION_WEST = new CountryId(240, "INMARSAT AOR-W", Type.Network, "INMARSAT_ATLANTIC_OCEAN_REGION_WEST", ""));
        list.add(INMARSAT_INDIAN_OCEAN_REGION = new CountryId(239, "INMARSAT IOR", Type.Network, "INMARSAT_INDIAN_OCEAN_REGION", ""));
        list.add(INMARSAT_PACIFIC_OCEAN_REGION = new CountryId(238, "INMARSAT POR", Type.Network, "INMARSAT_PACIFIC_OCEAN_REGION", ""));
        list.add(INMARSAT_SINGLE_NETWORK_ACCESS_CODE = new CountryId(241, "INMARSAT SNAC", Type.Network, "INMARSAT_SINGLE_NETWORK_ACCESS_CODE", ""));
        list.add(ICELAND = new CountryId(96, "Iceland", Type.Country, "ICELAND", "IS"));
        list.add(INDIA = new CountryId(97, "India", Type.Country, "INDIA", "IN"));
        list.add(INDONESIA = new CountryId(98, "Indonesia", Type.Country, "INDONESIA", "ID"));
        list.add(IRAN_ISLAMIC_REPUBLIC_OF = new CountryId(99, "Iran, Islamic Republic of", Type.Country, "IRAN_ISLAMIC_REPUBLIC_OF", "IR"));
        list.add(IRAQ = new CountryId(100, "Iraq", Type.Country, "IRAQ", "IQ"));
        list.add(IRELAND = new CountryId(101, "Ireland", Type.Country, "IRELAND", "IE"));
        list.add(IRIDIUM = new CountryId(235, "Iridium", Type.Network, "IRIDIUM", ""));
        list.add(ISRAEL = new CountryId(102, "Israel", Type.Country, "ISRAEL", "IL"));
        list.add(ITALY = new CountryId(103, "Italy", Type.Country, "ITALY", "IT"));        
        list.add(INUM_NUMBER = new CountryId(242, "iNum", Type.Network, "INUM", ""));        
        list.add(JAMAICA = new CountryId(104, "Jamaica", Type.Country, "JAMAICA", "JM"));
        list.add(JAPAN = new CountryId(105, "Japan", Type.Country, "JAPAN", "JP"));
        list.add(JORDAN = new CountryId(106, "Jordan", Type.Country, "JORDAN", "JO"));
        list.add(KAZAKSTAN = new CountryId(107, "Kazakstan", Type.Country, "KAZAKSTAN", "KZ"));
        list.add(KENYA = new CountryId(108, "Kenya", Type.Country, "KENYA", "KE"));
        list.add(KIRIBATI = new CountryId(109, "Kiribati", Type.Country, "KIRIBATI", "KI"));
        list.add(KOREA_DEMOCRATIC_PEOPLES_REPUBLIC_OF = new CountryId(110, "Korea, Democratic People's Republic of", Type.Country, "KOREA_DEMOCRATIC_PEOPLES_REPUBLIC_OF", "KP"));
        list.add(KOREA_REPUBLIC_OF = new CountryId(111, "Korea, Republic of", Type.Country, "KOREA_REPUBLIC_OF", "KR"));
        list.add(KUWAIT = new CountryId(112, "Kuwait", Type.Country, "KUWAIT", "KW"));
        list.add(KYRGYZSTAN = new CountryId(113, "Kyrgyzstan", Type.Country, "KYRGYZSTAN", "KG"));
        list.add(LAO_PEOPLES_DEMOCRATIC_REPUBLIC = new CountryId(114, "Lao People's Democratic Republic", Type.Country, "LAO_PEOPLES_DEMOCRATIC_REPUBLIC", "LA"));
        list.add(LATVIA = new CountryId(115, "Latvia", Type.Country, "LATVIA", "LV"));
        list.add(LEBANON = new CountryId(116, "Lebanon", Type.Country, "LEBANON", "LB"));
        list.add(LESOTHO = new CountryId(117, "Lesotho", Type.Country, "LESOTHO", "LS"));
        list.add(LIBERIA = new CountryId(118, "Liberia", Type.Country, "LIBERIA", "LR"));
        list.add(LIBYAN_ARAB_JAMAHIRIYA = new CountryId(119, "Libyan Arab Jamahiriya", Type.Country, "LIBYAN_ARAB_JAMAHIRIYA", "LY"));
        list.add(LIECHTENSTEIN = new CountryId(120, "Liechtenstein", Type.Country, "LIECHTENSTEIN", "LI"));
        list.add(LITHUANIA = new CountryId(121, "Lithuania", Type.Country, "LITHUANIA", "LT"));
        list.add(LUXEMBOURG = new CountryId(122, "Luxembourg", Type.Country, "LUXEMBOURG", "LU"));
        list.add(MACAU = new CountryId(123, "Macau", Type.Country, "MACAU", "MO"));
        list.add(MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF = new CountryId(124, "Macedonia, the Former Yugoslav Republic of", Type.Country, "MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF", "MK"));
        list.add(MADAGASCAR = new CountryId(125, "Madagascar", Type.Country, "MADAGASCAR", "MG"));
        list.add(MALAWI = new CountryId(126, "Malawi", Type.Country, "MALAWI", "MW"));
        list.add(MALAYSIA = new CountryId(127, "Malaysia", Type.Country, "MALAYSIA", "MY"));
        list.add(MALDIVES = new CountryId(128, "Maldives", Type.Country, "MALDIVES", "MV"));
        list.add(MALI = new CountryId(129, "Mali", Type.Country, "MALI", "ML"));
        list.add(MALTA = new CountryId(130, "Malta", Type.Country, "MALTA", "MT"));
        list.add(MARSHALL_ISLANDS = new CountryId(131, "Marshall Islands", Type.Country, "MARSHALL_ISLANDS", "MH"));
        list.add(MARTINIQUE = new CountryId(132, "Martinique", Type.Country, "MARTINIQUE", "MQ"));
        list.add(MAURITANIA = new CountryId(133, "Mauritania", Type.Country, "MAURITANIA", "MR"));
        list.add(MAURITIUS = new CountryId(134, "Mauritius", Type.Country, "MAURITIUS", "MU"));
        list.add(MAYOTTE = new CountryId(135, "Mayotte", Type.Country, "MAYOTTE", "YT"));
        list.add(MEXICO = new CountryId(136, "Mexico", Type.Country, "MEXICO", "MX"));
        list.add(MICRONESIA_FEDERATED_STATES_OF = new CountryId(137, "Micronesia, Federated States of", Type.Country, "MICRONESIA_FEDERATED_STATES_OF", "FM"));
        list.add(MOLDOVA_REPUBLIC_OF = new CountryId(138, "Moldova, Republic of", Type.Country, "MOLDOVA_REPUBLIC_OF", "MD"));
        list.add(MONACO = new CountryId(139, "Monaco", Type.Country, "MONACO", "MC"));
        list.add(MONGOLIA = new CountryId(140, "Mongolia", Type.Country, "MONGOLIA", "MN"));
        list.add(MONTSERRAT = new CountryId(141, "Montserrat", Type.Country, "MONTSERRAT", "MS"));
        list.add(MOROCCO = new CountryId(142, "Morocco", Type.Country, "MOROCCO", "MA"));
        list.add(MOZAMBIQUE = new CountryId(143, "Mozambique", Type.Country, "MOZAMBIQUE", "MZ"));
        list.add(MYANMAR = new CountryId(144, "Myanmar", Type.Country, "MYANMAR", "MM"));
        list.add(NAMIBIA = new CountryId(145, "Namibia", Type.Country, "NAMIBIA", "NA"));
        list.add(NAURU = new CountryId(146, "Nauru", Type.Country, "NAURU", "NR"));
        list.add(NEPAL = new CountryId(147, "Nepal", Type.Country, "NEPAL", "NP"));
        list.add(NETHERLANDS = new CountryId(148, "Netherlands", Type.Country, "NETHERLANDS", "NL"));
        list.add(NETHERLANDS_ANTILLES = new CountryId(149, "Netherlands Antilles", Type.Country, "NETHERLANDS_ANTILLES", "AN"));
        list.add(NEW_CALEDONIA = new CountryId(150, "New Caledonia", Type.Country, "NEW_CALEDONIA", "NC"));
        list.add(NEW_ZEALAND = new CountryId(151, "New Zealand", Type.Country, "NEW_ZEALAND", "NZ"));
        list.add(NICARAGUA = new CountryId(152, "Nicaragua", Type.Country, "NICARAGUA", "NI"));
        list.add(NIGER = new CountryId(153, "Niger", Type.Country, "NIGER", "NE"));
        list.add(NIGERIA = new CountryId(154, "Nigeria", Type.Country, "NIGERIA", "NG"));
        list.add(NIUE = new CountryId(155, "Niue", Type.Country, "NIUE", "NU"));
        list.add(NORFOLK_ISLAND = new CountryId(156, "Norfolk Island", Type.Country, "NORFOLK_ISLAND", "NF"));
        list.add(NORTHERN_MARIANA_ISLANDS = new CountryId(157, "Northern Mariana Islands", Type.Country, "NORTHERN_MARIANA_ISLANDS", "MP"));
        list.add(NORWAY = new CountryId(158, "Norway", Type.Country, "NORWAY", "NO"));
        list.add(OMAN = new CountryId(159, "Oman", Type.Country, "OMAN", "OM"));
        list.add(PAKISTAN = new CountryId(160, "Pakistan", Type.Country, "PAKISTAN", "PK"));
        list.add(PALAU = new CountryId(161, "Palau", Type.Country, "PALAU", "PW"));
        list.add(PALESTINIAN_TERRITORY_OCCUPIED = new CountryId(162, "Palestinian Territory, occupied", Type.Country, "PALESTINIAN_TERRITORY_OCCUPIED", "PS"));
        list.add(PANAMA = new CountryId(163, "Panama", Type.Country, "PANAMA", "PA"));
        list.add(PAPUA_NEW_GUINEA = new CountryId(164, "Papua New Guinea", Type.Country, "PAPUA_NEW_GUINEA", "PG"));
        list.add(PARAGUAY = new CountryId(165, "Paraguay", Type.Country, "PARAGUAY", "PY"));
        list.add(PERU = new CountryId(166, "Peru", Type.Country, "PERU", "PE"));
        list.add(PHILIPPINES = new CountryId(167, "Philippines", Type.Country, "PHILIPPINES", "PH"));
        list.add(POLAND = new CountryId(168, "Poland", Type.Country, "POLAND", "PL"));
        list.add(PORTUGAL = new CountryId(169, "Portugal", Type.Country, "PORTUGAL", "PT"));
        list.add(PUERTO_RICO = new CountryId(170, "Puerto Rico", Type.Country, "PUERTO_RICO", "PR"));
        list.add(QATAR = new CountryId(171, "Qatar", Type.Country, "QATAR", "QA"));
        list.add(REUNION = new CountryId(172, "Reunion", Type.Country, "REUNION", "RE"));
        list.add(ROMANIA = new CountryId(173, "Romania", Type.Country, "ROMANIA", "RO"));
        list.add(RUSSIAN_FEDERATION = new CountryId(174, "Russian Federation", Type.Country, "RUSSIAN_FEDERATION", "RU"));
        list.add(RWANDA = new CountryId(175, "Rwanda", Type.Country, "RWANDA", "RW"));
        list.add(SAINT_HELENA = new CountryId(176, "Saint Helena", Type.Country, "SAINT_HELENA", "SH"));
        list.add(SAINT_KITTS_AND_NEVIS = new CountryId(177, "Saint Kitts and Nevis", Type.Country, "SAINT_KITTS_AND_NEVIS", "KN"));
        list.add(SAINT_LUCIA = new CountryId(178, "Saint Lucia", Type.Country, "SAINT_LUCIA", "LC"));
        list.add(SAINT_PIERRE_AND_MIQUELON = new CountryId(179, "Saint Pierre and Miquelon", Type.Country, "SAINT_PIERRE_AND_MIQUELON", "PM"));
        list.add(SAINT_VINCENT_AND_THE_GRENADINES = new CountryId(180, "Saint Vincent and the Grenadines", Type.Country, "SAINT_VINCENT_AND_THE_GRENADINES", "VC"));
        list.add(SAMOA = new CountryId(181, "Samoa", Type.Country, "SAMOA", "WS"));
        list.add(SAN_MARINO = new CountryId(182, "San Marino", Type.Country, "SAN_MARINO", "SM"));
        list.add(SAO_TOME_AND_PRINCIPE = new CountryId(183, "Sao Tome and Principe", Type.Country, "SAO_TOME_AND_PRINCIPE", "ST"));
        list.add(SAUDI_ARABIA = new CountryId(184, "Saudi Arabia", Type.Country, "SAUDI_ARABIA", "SA"));
        list.add(SENEGAL = new CountryId(185, "Senegal", Type.Country, "SENEGAL", "SN"));
        list.add(SEYCHELLES = new CountryId(186, "Seychelles", Type.Country, "SEYCHELLES", "SC"));
        list.add(SIERRA_LEONE = new CountryId(187, "Sierra Leone", Type.Country, "SIERRA_LEONE", "SL"));
        list.add(SINGAPORE = new CountryId(188, "Singapore", Type.Country, "SINGAPORE", "SG"));
        list.add(SLOVAKIA = new CountryId(189, "Slovakia", Type.Country, "SLOVAKIA", "SK"));
        list.add(SLOVENIA = new CountryId(190, "Slovenia", Type.Country, "SLOVENIA", "SI"));
        list.add(SOLOMON_ISLANDS = new CountryId(191, "Solomon Islands", Type.Country, "SOLOMON_ISLANDS", "SB"));
        list.add(SOMALIA = new CountryId(192, "Somalia", Type.Country, "SOMALIA", "SO"));
        list.add(SOUTH_AFRICA = new CountryId(193, "South Africa", Type.Country, "SOUTH_AFRICA", "ZA"));
        list.add(SPAIN = new CountryId(194, "Spain", Type.Country, "SPAIN", "ES"));
        list.add(SRI_LANKA = new CountryId(195, "Sri Lanka", Type.Country, "SRI_LANKA", "LK"));
        list.add(SUDAN = new CountryId(196, "Sudan", Type.Country, "SUDAN", "SD"));
        list.add(SURINAME = new CountryId(197, "Suriname", Type.Country, "SURINAME", "SR"));
        list.add(SWAZILAND = new CountryId(198, "Swaziland", Type.Country, "SWAZILAND", "SZ"));
        list.add(SWEDEN = new CountryId(199, "Sweden", Type.Country, "SWEDEN", "SE"));
        list.add(SWITZERLAND = new CountryId(200, "Switzerland", Type.Country, "SWITZERLAND", "CH"));
        list.add(SYRIAN_ARAB_REPUBLIC = new CountryId(201, "Syrian Arab Republic", Type.Country, "SYRIAN_ARAB_REPUBLIC", "SY"));
        list.add(TAIWAN_PROVINCE_OF_CHINA = new CountryId(202, "Taiwan, Province of China", Type.Country, "TAIWAN_PROVINCE_OF_CHINA", "TW"));
        list.add(TAJIKISTAN = new CountryId(203, "Tajikistan", Type.Country, "TAJIKISTAN", "TJ"));
        list.add(TANZANIA_UNITED_REPUBLIC_OF = new CountryId(204, "Tanzania, United Republic of", Type.Country, "TANZANIA_UNITED_REPUBLIC_OF", "TZ"));
        list.add(THAILAND = new CountryId(205, "Thailand", Type.Country, "THAILAND", "TH"));
        list.add(TOGO = new CountryId(206, "Togo", Type.Country, "TOGO", "TG"));
        list.add(TOKELAU = new CountryId(207, "Tokelau", Type.Country, "TOKELAU", "TK"));
        list.add(TONGA = new CountryId(208, "Tonga", Type.Country, "TONGA", "TO"));
        list.add(TRINIDAD_AND_TOBAGO = new CountryId(209, "Trinidad and Tobago", Type.Country, "TRINIDAD_AND_TOBAGO", "TT"));
        list.add(TUNISIA = new CountryId(210, "Tunisia", Type.Country, "TUNISIA", "TN"));
        list.add(TURKEY = new CountryId(211, "Turkey", Type.Country, "TURKEY", "TR"));
        list.add(TURKMENISTAN = new CountryId(212, "Turkmenistan", Type.Country, "TURKMENISTAN", "TM"));
        list.add(TURKS_AND_CAICOS_ISLANDS = new CountryId(213, "Turks and Caicos Islands", Type.Country, "TURKS_AND_CAICOS_ISLANDS", "TC"));
        list.add(TUVALU = new CountryId(214, "Tuvalu", Type.Country, "TUVALU", "TV"));
        list.add(UGANDA = new CountryId(215, "Uganda", Type.Country, "UGANDA", "UG"));
        list.add(UKRAINE = new CountryId(216, "Ukraine", Type.Country, "UKRAINE", "UA"));
        list.add(UNITED_ARAB_EMIRATES = new CountryId(217, "United Arab Emirates", Type.Country, "UNITED_ARAB_EMIRATES", "AE"));
        list.add(UNITED_KINGDOM = new CountryId(218, "United Kingdom", Type.Country, "UNITED_KINGDOM", "GB"));
        list.add(UNITED_STATES = new CountryId(219, "United States", Type.Country, "UNITED_STATES", "US"));
        list.add(URUGUAY = new CountryId(220, "Uruguay", Type.Country, "URUGUAY", "UY"));
        list.add(UZBEKISTAN = new CountryId(221, "Uzbekistan", Type.Country, "UZBEKISTAN", "UZ"));
        list.add(VANUATU = new CountryId(222, "Vanuatu", Type.Country, "VANUATU", "VU"));
        list.add(VENEZUELA = new CountryId(223, "Venezuela", Type.Country, "VENEZUELA", "VE"));
        list.add(VIET_NAM = new CountryId(224, "Viet Nam", Type.Country, "VIET_NAM", "VN"));
        list.add(VIRGIN_ISLANDS_BRITISH = new CountryId(225, "Virgin Islands, British", Type.Country, "VIRGIN_ISLANDS_BRITISH", "VG"));
        list.add(VIRGIN_ISLANDS_U_S = new CountryId(226, "Virgin Islands, U.S.", Type.Country, "VIRGIN_ISLANDS_U_S", "VI"));
        list.add(WALLIS_AND_FUTUNA = new CountryId(227, "Wallis and Futuna", Type.Country, "WALLIS_AND_FUTUNA", "WF"));
        list.add(WESTERN_SAHARA = new CountryId(232, "Western Sahara", Type.Country, "WESTERN_SAHARA", "EH"));
        list.add(YEMEN = new CountryId(228, "Yemen", Type.Country, "YEMEN", "YE"));
        list.add(YUGOSLAVIA = new CountryId(229, "Yugoslavia", Type.Country, "YUGOSLAVIA", "YU"));
        list.add(ZAMBIA = new CountryId(230, "Zambia", Type.Country, "ZAMBIA", "ZM"));
        list.add(ZIMBABWE = new CountryId(231, "Zimbabwe", Type.Country, "ZIMBABWE", "ZW"));
        // Not in the list by default:
        _NOWHERE = new CountryId(1000, "(Nowhere; groups UTC timezones)", Type.Dummy, "__NOWHERE__");
        _UNDEFINED = new CountryId(-1, "(Undefined)", Type.Dummy, "__UNDEFINED__");
        //
        // Set up a Comparator to sort CountryIds by name, then sort
        //		
        {
            Comparator<CountryId> c = new Comparator<CountryId>() {

                @Override
                public int compare(CountryId o1, CountryId o2) {
                    return o1.getName().compareTo(o2.getName());
                }

            };
            Collections.sort(list, c);
        }

        //  		
        // Set up LIST_WITH_UNDEFINED, an unmodifiable list with '_UNDEFINED' in front of the list
        //
        {
            List<CountryId> newList = new ArrayList<CountryId>(list);
            newList.add(0, _UNDEFINED);
            LIST_WITH_UNDEFINED = Collections.unmodifiableList(newList);
        }
        //  		
        // Set up LIST_WITHOUT_UNDEFINED, an unmodifiable list without '_UNDEFINED'
        //		
        {
            List<CountryId> newList = new ArrayList<CountryId>(list);
            LIST_WITHOUT_UNDEFINED = Collections.unmodifiableList(newList);
        }
        //  		
        // Set up LIST_WITH_UNDEFINED_AND_NOWHERE, an unmodifiable list with '_NOWHERE'
        //		
        {
            List<CountryId> newList = new ArrayList<CountryId>(list);
            newList.add(0, _UNDEFINED);
            newList.add(_NOWHERE);
            LIST_WITH_UNDEFINED_AND_NOWHERE = Collections.unmodifiableList(newList);
        }

        //
        // Create a value->CountryId hash, and create MAP_BY_VALUE from it
        // ** This proecure checks whether a numeric id occurs twice, so we have nice self-checking code **  
        // Note that _UNDEFINED and _NOWHERE **are** taken up.
        //
        {
            Map<Integer, CountryId> mapByValue = new HashMap<Integer, CountryId>();
            for (CountryId cid : LIST_WITH_UNDEFINED_AND_NOWHERE) {
                if (mapByValue.containsKey(Integer.valueOf(cid.value))) {
                    throw new IllegalStateException("Key " + cid.value + " already seen");
                }
                mapByValue.put(Integer.valueOf(cid.value), cid);
            }
            MAP_BY_VALUE = Collections.unmodifiableMap(mapByValue);
        }
        //
        // Create a ISOCode->CountryId hash, and create MAP_BY_ISOCODE from it
        // ** This proecure checks whether an ISOCODE occurs twice, so we have nice self-checking code **  
        // Only "countries" in this list, so not satellites or dummies
        //
        {
            Map<String, CountryId> mapByIsoCode = new HashMap<String, CountryId>();
            for (CountryId cid : LIST_WITH_UNDEFINED_AND_NOWHERE) {
                if (Type.Country.equals(cid.type)) {
                    if (mapByIsoCode.containsKey(cid.isoCode)) {
                        throw new IllegalStateException("Key " + cid.isoCode + " already seen");
                    }
                    mapByIsoCode.put(cid.isoCode, cid);
                }
            }
            MAP_BY_ISOCODE = Collections.unmodifiableMap(mapByIsoCode);
        }
        //
        // Create a CanonicalName->CountryId hash, and create MAP_BY_CANONICAL_NAME from it
        // ** This proecure checks whether an Canonical Name occurs twice, so we have nice self-checking code **   
        //
        {
            Map<String, CountryId> mapByCanoName = new HashMap<String, CountryId>();
            for (CountryId cid : LIST_WITH_UNDEFINED_AND_NOWHERE) {
                if (Type.Country.equals(cid.type) || Type.Network.equals(cid.type)) {
                    if (mapByCanoName.containsKey(cid.canoName)) {
                        throw new IllegalStateException("Key " + cid.canoName + " already seen");
                    }
                    mapByCanoName.put(cid.canoName, cid);
                }
            }
            MAP_BY_CANONICAL_NAME = Collections.unmodifiableMap(mapByCanoName);
        }
        /*
         * for (CountryId cid : LIST_WITH_UNDEFINED_AND_NOWHERE) {
         * System.out.printf("list.add(%s = new CountryId(%d, \"%s\", Type.%s, \"%s\", \"%s\"));\n",
         * cid.getName().toUpperCase(), cid.getValue(), cid.getName(), cid.getType(), cid.getName() .toUpperCase(),
         * (cid.getIsoCode() == null ? "" : cid.getIsoCode()));
         * 
         * }
         */
    }

    /**
     * The constructor, can only called by this class. The class defines the possible cases using the constructor and
     * that's it
     */

    private CountryId(int value, String name, Type type, String canoName, String... more) {
        assert type != null;
        assert canoName != null;
        assert canoName.matches("^[A-Z_]+$") : canoName;
        assert name != null;
        assert !"".equals(name.trim());
        assert Type.Dummy.equals(type) || value > 0;
        this.value = value;
        this.name = name.trim();
        this.type = type;
        this.canoName = canoName.trim().toLowerCase(); // canonical names are lowercase
        if (Type.Country.equals(type)) {
            assert more.length > 0;
            assert more[0] != null;
            this.isoCode = more[0].trim().toUpperCase(); // iso codes are uppercase
            assert this.isoCode.length() == 2;
        } else {
            this.isoCode = null;
        }
    }

    /**
     * Accessor: the iso code. Returns null if this is not a "country"
     */

    public String getIsoCode() {
        return isoCode;
    }

    /**
     * Accessor: the type. Never returns null
     */

    public Type getType() {
        return type;
    }

    /**
     * Accessor: The Canonical Name. Never returns null
     */

    public String getCanonicalName() {
        return canoName;
    }

    /**
     * Transform this value into a string (i.e. get the "name" -- not the canonical name)
     */

    @Override
    public String toString() {
        return name;
    }

    /**
     * Is a "value" valid? _UNDEFINED and _NOWHERE are considered valid! null can be passed and will return false.
     */

    public static boolean isValid(Integer x) {
        return (MAP_BY_VALUE.containsKey(x));
    }

    public static boolean isValid(int x) {
        return (MAP_BY_VALUE.containsKey(Integer.valueOf(x)));
    }

    /**
     * Comparison compares the underlying values
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // quick guess
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CountryId)) {
            return false;
        }
        CountryId other = (CountryId) obj;
        return other.value == this.value;
    }

    /**
     * Get the actual underlying value (use sparingly, for writing to store)
     */

    @Override
    public int getValue() {
        return value;
    }

    /**
     * Return an instance given an int value 'x'. Throws 'IllegalArgumentException' if there is no instance
     * corresponding to 'x' The identifier of _UNDEFINED and _NOWHERE are acceptable as are other valid CountryId
     * identifiers. Any other identifier is not. Throws if not found.
     */

    public static CountryId obtain(int x) {
        return obtain(Integer.valueOf(x));
    }

    /**
     * Obtain from an Integer. Do not pass (null). The identifier of _UNDEFINED and _NOWHERE are acceptable as are other
     * valid CountryId identifiers. Any other identifier is not. Throws if not found.
     */

    public static CountryId obtain(Integer x) {
        Check.notNull(x,"integer");
        CountryId res = MAP_BY_VALUE.get(x);
        // Undefined is in the map, so obtain(-1) will yield res!=null
        if (res == null) {
            throw new IllegalArgumentException("Nothing corresponding to value '" + x + "' exists");
        } else {
            return res;
        }
    }

    /**
     * Pass a *stringified integer* 'x', which is parsed and used for retrieval. Do not pass (null). The identifier of
     * _UNDEFINED and _NOWHERE are acceptable as are other valid CountryId identifiers. Any other identifier is not.
     * Throws if not found.
     */

    public static CountryId obtainFromIntegerString(String x) {
        Check.notNull(x,"string");
        int v;
        try {
            v = Integer.parseInt(x);
        } catch (NumberFormatException exe) {
            throw new IllegalArgumentException("The passed String does not represent an integer: '" + x + "'");
        }
        return obtain(v); // may throw
    }

    /**
     * Pass the *canonical name* 'x', which is parsed and used for retrieval. Do not pass (null). Throws if not found or
     * returns null, depending on the "throwIfNotFound" boolean. NOWHERE and UNDEFINED won't give results here.
     */

    public static CountryId obtainFromCanonicalName(String canoName, boolean throwIfNotFound) {
        if (canoName == null) {
            throw new IllegalArgumentException("The passed 'canonical name' is (null)");
        }
        String myCanoName = canoName.trim().toLowerCase();
        CountryId res = MAP_BY_CANONICAL_NAME.get(myCanoName);
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("Nothing corresponding to canonical name '" + canoName + "' exists");
        } else {
            return res;
        }
    }

    /**
     * Wanna hash this...
     */

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * Obtain the short human-readable text. Same as toString()
     */

    public String getName() {
        return name;
    }

}
