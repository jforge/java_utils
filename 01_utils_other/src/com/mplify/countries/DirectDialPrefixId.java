package com.mplify.countries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.mplify.enums.EnumerativeTypeUsingString;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that is used to represent 
 * 
 * -->> international direct dialing prefixes
 * 
 * Formerly a database table was used but that does not seems to be necessary
 * (some leftover from the P&T ICMS days). Also, using a symbolic id instead
 * of an 'int' is a big improvement as the compiler can do better checks.
 * 
 * 2005.01.30 - Created from database table. The idea is to have better typed
 *              values than just a 'int'
 * 2005.01.31 - Static constructor bettered.
 * 2005.02.01 - Unnecessary numeric ids thrown out.
 * 2009.01.07 - Review: Added Satellite network DD codes, threw 
 * 2010.12.14 - Added Country code +883, an "iNum"
 *              https://evolution.voxeo.com/inum/
 * 2011.10.19 - Adapted for LOG4J --> SLF4J migration
 ******************************************************************************/

public class DirectDialPrefixId implements EnumerativeTypeUsingString {

//    private final static String CLASS = DirectDialPrefixId.class.getName();
//    private final static Logger LOGGER_obtain = LoggerFactory.getLogger(CLASS + ".obtain");

    /*
     * The actual direct dial prefix, contains only digits, does not start with a '+' or '0'
     */

    private final String ddprefix;

    /*
     * These are the possible instance of this class; they form a quasi-enumeration These are public and used by client
     * code. Notice that the name of the id is derived from the direct dial prefix.
     */

    public final static DirectDialPrefixId CC_93;
    public final static DirectDialPrefixId CC_355;
    public final static DirectDialPrefixId CC_213;
    public final static DirectDialPrefixId CC_684;
    public final static DirectDialPrefixId CC_376;
    public final static DirectDialPrefixId CC_244;
    public final static DirectDialPrefixId CC_1264;
    public final static DirectDialPrefixId CC_672;
    public final static DirectDialPrefixId CC_1268;
    public final static DirectDialPrefixId CC_54;
    public final static DirectDialPrefixId CC_374;
    public final static DirectDialPrefixId CC_297;
    public final static DirectDialPrefixId CC_61;
    public final static DirectDialPrefixId CC_43;
    public final static DirectDialPrefixId CC_994;
    public final static DirectDialPrefixId CC_1242;
    public final static DirectDialPrefixId CC_973;
    public final static DirectDialPrefixId CC_880;
    public final static DirectDialPrefixId CC_246;
    public final static DirectDialPrefixId CC_375;
    public final static DirectDialPrefixId CC_32;
    public final static DirectDialPrefixId CC_501;
    public final static DirectDialPrefixId CC_229;
    public final static DirectDialPrefixId CC_1441;
    public final static DirectDialPrefixId CC_975;
    public final static DirectDialPrefixId CC_591;
    public final static DirectDialPrefixId CC_387;
    public final static DirectDialPrefixId CC_267;
    public final static DirectDialPrefixId CC_55;
    public final static DirectDialPrefixId CC_673;
    public final static DirectDialPrefixId CC_359;
    public final static DirectDialPrefixId CC_226;
    public final static DirectDialPrefixId CC_257;
    public final static DirectDialPrefixId CC_855;
    public final static DirectDialPrefixId CC_237;
    public final static DirectDialPrefixId CC_238;
    public final static DirectDialPrefixId CC_345;
    public final static DirectDialPrefixId CC_236;
    public final static DirectDialPrefixId CC_235;
    public final static DirectDialPrefixId CC_56;
    public final static DirectDialPrefixId CC_86;
    public final static DirectDialPrefixId CC_57;
    public final static DirectDialPrefixId CC_269;
    public final static DirectDialPrefixId CC_242;
    public final static DirectDialPrefixId CC_243;
    public final static DirectDialPrefixId CC_682;
    public final static DirectDialPrefixId CC_506;
    public final static DirectDialPrefixId CC_225;
    public final static DirectDialPrefixId CC_385;
    public final static DirectDialPrefixId CC_53;
    public final static DirectDialPrefixId CC_357;
    public final static DirectDialPrefixId CC_420;
    public final static DirectDialPrefixId CC_45;
    public final static DirectDialPrefixId CC_253;
    public final static DirectDialPrefixId CC_1767;
    public final static DirectDialPrefixId CC_1809;
    public final static DirectDialPrefixId CC_670;
    public final static DirectDialPrefixId CC_593;
    public final static DirectDialPrefixId CC_20;
    public final static DirectDialPrefixId CC_503;
    public final static DirectDialPrefixId CC_240;
    public final static DirectDialPrefixId CC_291;
    public final static DirectDialPrefixId CC_372;
    public final static DirectDialPrefixId CC_251;
    public final static DirectDialPrefixId CC_500;
    public final static DirectDialPrefixId CC_298;
    public final static DirectDialPrefixId CC_679;
    public final static DirectDialPrefixId CC_358;
    public final static DirectDialPrefixId CC_33;
    public final static DirectDialPrefixId CC_594;
    public final static DirectDialPrefixId CC_689;
    public final static DirectDialPrefixId CC_260;
    public final static DirectDialPrefixId CC_241;
    public final static DirectDialPrefixId CC_220;
    public final static DirectDialPrefixId CC_995;
    public final static DirectDialPrefixId CC_49;
    public final static DirectDialPrefixId CC_233;
    public final static DirectDialPrefixId CC_350;
    public final static DirectDialPrefixId CC_30;
    public final static DirectDialPrefixId CC_299;
    public final static DirectDialPrefixId CC_1473;
    public final static DirectDialPrefixId CC_590;
    public final static DirectDialPrefixId CC_1671;
    public final static DirectDialPrefixId CC_502;
    public final static DirectDialPrefixId CC_224;
    public final static DirectDialPrefixId CC_245;
    public final static DirectDialPrefixId CC_592;
    public final static DirectDialPrefixId CC_509;
    public final static DirectDialPrefixId CC_379;
    public final static DirectDialPrefixId CC_504;
    public final static DirectDialPrefixId CC_852;
    public final static DirectDialPrefixId CC_36;
    public final static DirectDialPrefixId CC_354;
    public final static DirectDialPrefixId CC_91;
    public final static DirectDialPrefixId CC_62;
    public final static DirectDialPrefixId CC_98;
    public final static DirectDialPrefixId CC_964;
    public final static DirectDialPrefixId CC_353;
    public final static DirectDialPrefixId CC_972;
    public final static DirectDialPrefixId CC_39;
    public final static DirectDialPrefixId CC_1876;
    public final static DirectDialPrefixId CC_81;
    public final static DirectDialPrefixId CC_962;
    public final static DirectDialPrefixId CC_254;
    public final static DirectDialPrefixId CC_686;
    public final static DirectDialPrefixId CC_850;
    public final static DirectDialPrefixId CC_82;
    public final static DirectDialPrefixId CC_965;
    public final static DirectDialPrefixId CC_996;
    public final static DirectDialPrefixId CC_856;
    public final static DirectDialPrefixId CC_371;
    public final static DirectDialPrefixId CC_961;
    public final static DirectDialPrefixId CC_266;
    public final static DirectDialPrefixId CC_231;
    public final static DirectDialPrefixId CC_218;
    public final static DirectDialPrefixId CC_423;
    public final static DirectDialPrefixId CC_370;
    public final static DirectDialPrefixId CC_352;
    public final static DirectDialPrefixId CC_853;
    public final static DirectDialPrefixId CC_389;
    public final static DirectDialPrefixId CC_261;
    public final static DirectDialPrefixId CC_265;
    public final static DirectDialPrefixId CC_60;
    public final static DirectDialPrefixId CC_960;
    public final static DirectDialPrefixId CC_223;
    public final static DirectDialPrefixId CC_356;
    public final static DirectDialPrefixId CC_692;
    public final static DirectDialPrefixId CC_596;
    public final static DirectDialPrefixId CC_222;
    public final static DirectDialPrefixId CC_230;
    public final static DirectDialPrefixId CC_52;
    public final static DirectDialPrefixId CC_691;
    public final static DirectDialPrefixId CC_373;
    public final static DirectDialPrefixId CC_377;
    public final static DirectDialPrefixId CC_976;
    public final static DirectDialPrefixId CC_1664;
    public final static DirectDialPrefixId CC_212;
    public final static DirectDialPrefixId CC_258;
    public final static DirectDialPrefixId CC_95;
    public final static DirectDialPrefixId CC_264;
    public final static DirectDialPrefixId CC_674;
    public final static DirectDialPrefixId CC_977;
    public final static DirectDialPrefixId CC_31;
    public final static DirectDialPrefixId CC_599;
    public final static DirectDialPrefixId CC_687;
    public final static DirectDialPrefixId CC_64;
    public final static DirectDialPrefixId CC_505;
    public final static DirectDialPrefixId CC_227;
    public final static DirectDialPrefixId CC_234;
    public final static DirectDialPrefixId CC_683;
    public final static DirectDialPrefixId CC_1670;
    public final static DirectDialPrefixId CC_47;
    public final static DirectDialPrefixId CC_968;
    public final static DirectDialPrefixId CC_92;
    public final static DirectDialPrefixId CC_680;
    public final static DirectDialPrefixId CC_970;
    public final static DirectDialPrefixId CC_507;
    public final static DirectDialPrefixId CC_675;
    public final static DirectDialPrefixId CC_595;
    public final static DirectDialPrefixId CC_51;
    public final static DirectDialPrefixId CC_63;
    public final static DirectDialPrefixId CC_48;
    public final static DirectDialPrefixId CC_351;
    public final static DirectDialPrefixId CC_1787;
    public final static DirectDialPrefixId CC_974;
    public final static DirectDialPrefixId CC_262;
    public final static DirectDialPrefixId CC_40;
    public final static DirectDialPrefixId CC_7;
    public final static DirectDialPrefixId CC_250;
    public final static DirectDialPrefixId CC_290;
    public final static DirectDialPrefixId CC_1869;
    public final static DirectDialPrefixId CC_1758;
    public final static DirectDialPrefixId CC_508;
    public final static DirectDialPrefixId CC_1784;
    public final static DirectDialPrefixId CC_685;
    public final static DirectDialPrefixId CC_378;
    public final static DirectDialPrefixId CC_239;
    public final static DirectDialPrefixId CC_966;
    public final static DirectDialPrefixId CC_221;
    public final static DirectDialPrefixId CC_248;
    public final static DirectDialPrefixId CC_232;
    public final static DirectDialPrefixId CC_65;
    public final static DirectDialPrefixId CC_421;
    public final static DirectDialPrefixId CC_386;
    public final static DirectDialPrefixId CC_677;
    public final static DirectDialPrefixId CC_252;
    public final static DirectDialPrefixId CC_27;
    public final static DirectDialPrefixId CC_34;
    public final static DirectDialPrefixId CC_94;
    public final static DirectDialPrefixId CC_249;
    public final static DirectDialPrefixId CC_597;
    public final static DirectDialPrefixId CC_268;
    public final static DirectDialPrefixId CC_46;
    public final static DirectDialPrefixId CC_41;
    public final static DirectDialPrefixId CC_963;
    public final static DirectDialPrefixId CC_886;
    public final static DirectDialPrefixId CC_992;
    public final static DirectDialPrefixId CC_255;
    public final static DirectDialPrefixId CC_66;
    public final static DirectDialPrefixId CC_228;
    public final static DirectDialPrefixId CC_690;
    public final static DirectDialPrefixId CC_676;
    public final static DirectDialPrefixId CC_1868;
    public final static DirectDialPrefixId CC_216;
    public final static DirectDialPrefixId CC_90;
    public final static DirectDialPrefixId CC_993;
    public final static DirectDialPrefixId CC_1649;
    public final static DirectDialPrefixId CC_688;
    public final static DirectDialPrefixId CC_256;
    public final static DirectDialPrefixId CC_380;
    public final static DirectDialPrefixId CC_971;
    public final static DirectDialPrefixId CC_44;
    public final static DirectDialPrefixId CC_1;
    public final static DirectDialPrefixId CC_598;
    public final static DirectDialPrefixId CC_998;
    public final static DirectDialPrefixId CC_678;
    public final static DirectDialPrefixId CC_58;
    public final static DirectDialPrefixId CC_84;
    public final static DirectDialPrefixId CC_1284;
    public final static DirectDialPrefixId CC_1340;
    public final static DirectDialPrefixId CC_681;
    public final static DirectDialPrefixId CC_967;
    public final static DirectDialPrefixId CC_381;
    public final static DirectDialPrefixId CC_263;
    public final static DirectDialPrefixId CC_788;
    public final static DirectDialPrefixId CC_1246;
    public final static DirectDialPrefixId CC_1204;
    public final static DirectDialPrefixId CC_1306;
    public final static DirectDialPrefixId CC_1403;
    public final static DirectDialPrefixId CC_1416;
    public final static DirectDialPrefixId CC_1418;
    public final static DirectDialPrefixId CC_1506;
    public final static DirectDialPrefixId CC_1514;
    public final static DirectDialPrefixId CC_1519;
    public final static DirectDialPrefixId CC_1600;
    public final static DirectDialPrefixId CC_1604;
    public final static DirectDialPrefixId CC_1613;
    public final static DirectDialPrefixId CC_1705;
    public final static DirectDialPrefixId CC_1709;
    public final static DirectDialPrefixId CC_1807;
    public final static DirectDialPrefixId CC_1819;
    public final static DirectDialPrefixId CC_1902;
    public final static DirectDialPrefixId CC_1905;
    public final static DirectDialPrefixId CC_1867;
    public final static DirectDialPrefixId CC_1250;
    public final static DirectDialPrefixId CC_1345;
    public final static DirectDialPrefixId CC_731;
    public final static DirectDialPrefixId CC_732;
    public final static DirectDialPrefixId CC_7330;
    public final static DirectDialPrefixId CC_7336;
    public final static DirectDialPrefixId CC_704;
    public final static DirectDialPrefixId CC_703;
    public final static DirectDialPrefixId CC_705;
    public final static DirectDialPrefixId CC_706;
    public final static DirectDialPrefixId CC_755;
    public final static DirectDialPrefixId CC_8810;
    public final static DirectDialPrefixId CC_8811;
    public final static DirectDialPrefixId CC_8812;
    public final static DirectDialPrefixId CC_8813;
    public final static DirectDialPrefixId CC_8816;
    public final static DirectDialPrefixId CC_8817;
    public final static DirectDialPrefixId CC_8818;
    public final static DirectDialPrefixId CC_8819;
    public final static DirectDialPrefixId CC_871;
    public final static DirectDialPrefixId CC_872;
    public final static DirectDialPrefixId CC_873;
    public final static DirectDialPrefixId CC_874;
    public final static DirectDialPrefixId CC_870;
    public final static DirectDialPrefixId CC_883;

    /*
     * A list of the values, public but immutable
     */

    public final static List<DirectDialPrefixId> LIST;

    /*
     * The values hashed by their actual prefixes, immutabée
     */

    private final static Map<String, DirectDialPrefixId> MAP_BY_PREFIX;
    private final static int MAX_PREFIX_LENGTH;

    /*
     * Init
     */

    static {
        List<DirectDialPrefixId> list = new ArrayList<DirectDialPrefixId>();
        list.add(CC_93 = new DirectDialPrefixId("93"));
        list.add(CC_355 = new DirectDialPrefixId("355"));
        list.add(CC_213 = new DirectDialPrefixId("213"));
        list.add(CC_684 = new DirectDialPrefixId("684"));
        list.add(CC_376 = new DirectDialPrefixId("376"));
        list.add(CC_244 = new DirectDialPrefixId("244"));
        list.add(CC_1264 = new DirectDialPrefixId("1264"));
        list.add(CC_672 = new DirectDialPrefixId("672"));
        list.add(CC_1268 = new DirectDialPrefixId("1268"));
        list.add(CC_54 = new DirectDialPrefixId("54"));
        list.add(CC_374 = new DirectDialPrefixId("374"));
        list.add(CC_297 = new DirectDialPrefixId("297"));
        list.add(CC_61 = new DirectDialPrefixId("61"));
        list.add(CC_43 = new DirectDialPrefixId("43"));
        list.add(CC_994 = new DirectDialPrefixId("994"));
        list.add(CC_1242 = new DirectDialPrefixId("1242"));
        list.add(CC_973 = new DirectDialPrefixId("973"));
        list.add(CC_880 = new DirectDialPrefixId("880"));
        list.add(CC_246 = new DirectDialPrefixId("246"));
        list.add(CC_375 = new DirectDialPrefixId("375"));
        list.add(CC_32 = new DirectDialPrefixId("32"));
        list.add(CC_501 = new DirectDialPrefixId("501"));
        list.add(CC_229 = new DirectDialPrefixId("229"));
        list.add(CC_1441 = new DirectDialPrefixId("1441"));
        list.add(CC_975 = new DirectDialPrefixId("975"));
        list.add(CC_591 = new DirectDialPrefixId("591"));
        list.add(CC_387 = new DirectDialPrefixId("387"));
        list.add(CC_267 = new DirectDialPrefixId("267"));
        list.add(CC_55 = new DirectDialPrefixId("55"));
        list.add(CC_673 = new DirectDialPrefixId("673"));
        list.add(CC_359 = new DirectDialPrefixId("359"));
        list.add(CC_226 = new DirectDialPrefixId("226"));
        list.add(CC_257 = new DirectDialPrefixId("257"));
        list.add(CC_855 = new DirectDialPrefixId("855"));
        list.add(CC_237 = new DirectDialPrefixId("237"));
        list.add(CC_238 = new DirectDialPrefixId("238"));
        list.add(CC_345 = new DirectDialPrefixId("345"));
        list.add(CC_236 = new DirectDialPrefixId("236"));
        list.add(CC_235 = new DirectDialPrefixId("235"));
        list.add(CC_56 = new DirectDialPrefixId("56"));
        list.add(CC_86 = new DirectDialPrefixId("86"));
        list.add(CC_57 = new DirectDialPrefixId("57"));
        list.add(CC_269 = new DirectDialPrefixId("269"));
        list.add(CC_242 = new DirectDialPrefixId("242"));
        list.add(CC_243 = new DirectDialPrefixId("243"));
        list.add(CC_682 = new DirectDialPrefixId("682"));
        list.add(CC_506 = new DirectDialPrefixId("506"));
        list.add(CC_225 = new DirectDialPrefixId("225"));
        list.add(CC_385 = new DirectDialPrefixId("385"));
        list.add(CC_53 = new DirectDialPrefixId("53"));
        list.add(CC_357 = new DirectDialPrefixId("357"));
        list.add(CC_420 = new DirectDialPrefixId("420"));
        list.add(CC_45 = new DirectDialPrefixId("45"));
        list.add(CC_253 = new DirectDialPrefixId("253"));
        list.add(CC_1767 = new DirectDialPrefixId("1767"));
        list.add(CC_1809 = new DirectDialPrefixId("1809"));
        list.add(CC_670 = new DirectDialPrefixId("670"));
        list.add(CC_593 = new DirectDialPrefixId("593"));
        list.add(CC_20 = new DirectDialPrefixId("20"));
        list.add(CC_503 = new DirectDialPrefixId("503"));
        list.add(CC_240 = new DirectDialPrefixId("240"));
        list.add(CC_291 = new DirectDialPrefixId("291"));
        list.add(CC_372 = new DirectDialPrefixId("372"));
        list.add(CC_251 = new DirectDialPrefixId("251"));
        list.add(CC_500 = new DirectDialPrefixId("500"));
        list.add(CC_298 = new DirectDialPrefixId("298"));
        list.add(CC_679 = new DirectDialPrefixId("679"));
        list.add(CC_358 = new DirectDialPrefixId("358"));
        list.add(CC_33 = new DirectDialPrefixId("33"));
        list.add(CC_594 = new DirectDialPrefixId("594"));
        list.add(CC_689 = new DirectDialPrefixId("689"));
        list.add(CC_260 = new DirectDialPrefixId("260"));
        list.add(CC_241 = new DirectDialPrefixId("241"));
        list.add(CC_220 = new DirectDialPrefixId("220"));
        list.add(CC_995 = new DirectDialPrefixId("995"));
        list.add(CC_49 = new DirectDialPrefixId("49"));
        list.add(CC_233 = new DirectDialPrefixId("233"));
        list.add(CC_350 = new DirectDialPrefixId("350"));
        list.add(CC_30 = new DirectDialPrefixId("30"));
        list.add(CC_299 = new DirectDialPrefixId("299"));
        list.add(CC_1473 = new DirectDialPrefixId("1473"));
        list.add(CC_590 = new DirectDialPrefixId("590"));
        list.add(CC_1671 = new DirectDialPrefixId("1671"));
        list.add(CC_502 = new DirectDialPrefixId("502"));
        list.add(CC_224 = new DirectDialPrefixId("224"));
        list.add(CC_245 = new DirectDialPrefixId("245"));
        list.add(CC_592 = new DirectDialPrefixId("592"));
        list.add(CC_509 = new DirectDialPrefixId("509"));
        list.add(CC_379 = new DirectDialPrefixId("379"));
        list.add(CC_504 = new DirectDialPrefixId("504"));
        list.add(CC_852 = new DirectDialPrefixId("852"));
        list.add(CC_36 = new DirectDialPrefixId("36"));
        list.add(CC_354 = new DirectDialPrefixId("354"));
        list.add(CC_91 = new DirectDialPrefixId("91"));
        list.add(CC_62 = new DirectDialPrefixId("62"));
        list.add(CC_98 = new DirectDialPrefixId("98"));
        list.add(CC_964 = new DirectDialPrefixId("964"));
        list.add(CC_353 = new DirectDialPrefixId("353"));
        list.add(CC_972 = new DirectDialPrefixId("972"));
        list.add(CC_39 = new DirectDialPrefixId("39"));
        list.add(CC_1876 = new DirectDialPrefixId("1876"));
        list.add(CC_81 = new DirectDialPrefixId("81"));
        list.add(CC_962 = new DirectDialPrefixId("962"));
        list.add(CC_254 = new DirectDialPrefixId("254"));
        list.add(CC_686 = new DirectDialPrefixId("686"));
        list.add(CC_850 = new DirectDialPrefixId("850"));
        list.add(CC_82 = new DirectDialPrefixId("82"));
        list.add(CC_965 = new DirectDialPrefixId("965"));
        list.add(CC_996 = new DirectDialPrefixId("996"));
        list.add(CC_856 = new DirectDialPrefixId("856"));
        list.add(CC_371 = new DirectDialPrefixId("371"));
        list.add(CC_961 = new DirectDialPrefixId("961"));
        list.add(CC_266 = new DirectDialPrefixId("266"));
        list.add(CC_231 = new DirectDialPrefixId("231"));
        list.add(CC_218 = new DirectDialPrefixId("218"));
        list.add(CC_423 = new DirectDialPrefixId("423"));
        list.add(CC_370 = new DirectDialPrefixId("370"));
        list.add(CC_352 = new DirectDialPrefixId("352"));
        list.add(CC_853 = new DirectDialPrefixId("853"));
        list.add(CC_389 = new DirectDialPrefixId("389"));
        list.add(CC_261 = new DirectDialPrefixId("261"));
        list.add(CC_265 = new DirectDialPrefixId("265"));
        list.add(CC_60 = new DirectDialPrefixId("60"));
        list.add(CC_960 = new DirectDialPrefixId("960"));
        list.add(CC_223 = new DirectDialPrefixId("223"));
        list.add(CC_356 = new DirectDialPrefixId("356"));
        list.add(CC_692 = new DirectDialPrefixId("692"));
        list.add(CC_596 = new DirectDialPrefixId("596"));
        list.add(CC_222 = new DirectDialPrefixId("222"));
        list.add(CC_230 = new DirectDialPrefixId("230"));
        list.add(CC_52 = new DirectDialPrefixId("52"));
        list.add(CC_691 = new DirectDialPrefixId("691"));
        list.add(CC_373 = new DirectDialPrefixId("373"));
        list.add(CC_377 = new DirectDialPrefixId("377"));
        list.add(CC_976 = new DirectDialPrefixId("976"));
        list.add(CC_1664 = new DirectDialPrefixId("1664"));
        list.add(CC_212 = new DirectDialPrefixId("212"));
        list.add(CC_258 = new DirectDialPrefixId("258"));
        list.add(CC_95 = new DirectDialPrefixId("95"));
        list.add(CC_264 = new DirectDialPrefixId("264"));
        list.add(CC_674 = new DirectDialPrefixId("674"));
        list.add(CC_977 = new DirectDialPrefixId("977"));
        list.add(CC_31 = new DirectDialPrefixId("31"));
        list.add(CC_599 = new DirectDialPrefixId("599"));
        list.add(CC_687 = new DirectDialPrefixId("687"));
        list.add(CC_64 = new DirectDialPrefixId("64"));
        list.add(CC_505 = new DirectDialPrefixId("505"));
        list.add(CC_227 = new DirectDialPrefixId("227"));
        list.add(CC_234 = new DirectDialPrefixId("234"));
        list.add(CC_683 = new DirectDialPrefixId("683"));
        list.add(CC_1670 = new DirectDialPrefixId("1670"));
        list.add(CC_47 = new DirectDialPrefixId("47"));
        list.add(CC_968 = new DirectDialPrefixId("968"));
        list.add(CC_92 = new DirectDialPrefixId("92"));
        list.add(CC_680 = new DirectDialPrefixId("680"));
        list.add(CC_970 = new DirectDialPrefixId("970"));
        list.add(CC_507 = new DirectDialPrefixId("507"));
        list.add(CC_675 = new DirectDialPrefixId("675"));
        list.add(CC_595 = new DirectDialPrefixId("595"));
        list.add(CC_51 = new DirectDialPrefixId("51"));
        list.add(CC_63 = new DirectDialPrefixId("63"));
        list.add(CC_48 = new DirectDialPrefixId("48"));
        list.add(CC_351 = new DirectDialPrefixId("351"));
        list.add(CC_1787 = new DirectDialPrefixId("1787"));
        list.add(CC_974 = new DirectDialPrefixId("974"));
        list.add(CC_262 = new DirectDialPrefixId("262"));
        list.add(CC_40 = new DirectDialPrefixId("40"));
        list.add(CC_7 = new DirectDialPrefixId("7"));
        list.add(CC_250 = new DirectDialPrefixId("250"));
        list.add(CC_290 = new DirectDialPrefixId("290"));
        list.add(CC_1869 = new DirectDialPrefixId("1869"));
        list.add(CC_1758 = new DirectDialPrefixId("1758"));
        list.add(CC_508 = new DirectDialPrefixId("508"));
        list.add(CC_1784 = new DirectDialPrefixId("1784"));
        list.add(CC_685 = new DirectDialPrefixId("685"));
        list.add(CC_378 = new DirectDialPrefixId("378"));
        list.add(CC_239 = new DirectDialPrefixId("239"));
        list.add(CC_966 = new DirectDialPrefixId("966"));
        list.add(CC_221 = new DirectDialPrefixId("221"));
        list.add(CC_248 = new DirectDialPrefixId("248"));
        list.add(CC_232 = new DirectDialPrefixId("232"));
        list.add(CC_65 = new DirectDialPrefixId("65"));
        list.add(CC_421 = new DirectDialPrefixId("421"));
        list.add(CC_386 = new DirectDialPrefixId("386"));
        list.add(CC_677 = new DirectDialPrefixId("677"));
        list.add(CC_252 = new DirectDialPrefixId("252"));
        list.add(CC_27 = new DirectDialPrefixId("27"));
        list.add(CC_34 = new DirectDialPrefixId("34"));
        list.add(CC_94 = new DirectDialPrefixId("94"));
        list.add(CC_249 = new DirectDialPrefixId("249"));
        list.add(CC_597 = new DirectDialPrefixId("597"));
        list.add(CC_268 = new DirectDialPrefixId("268"));
        list.add(CC_46 = new DirectDialPrefixId("46"));
        list.add(CC_41 = new DirectDialPrefixId("41"));
        list.add(CC_963 = new DirectDialPrefixId("963"));
        list.add(CC_886 = new DirectDialPrefixId("886"));
        list.add(CC_992 = new DirectDialPrefixId("992"));
        list.add(CC_255 = new DirectDialPrefixId("255"));
        list.add(CC_66 = new DirectDialPrefixId("66"));
        list.add(CC_228 = new DirectDialPrefixId("228"));
        list.add(CC_690 = new DirectDialPrefixId("690"));
        list.add(CC_676 = new DirectDialPrefixId("676"));
        list.add(CC_1868 = new DirectDialPrefixId("1868"));
        list.add(CC_216 = new DirectDialPrefixId("216"));
        list.add(CC_90 = new DirectDialPrefixId("90"));
        list.add(CC_993 = new DirectDialPrefixId("993"));
        list.add(CC_1649 = new DirectDialPrefixId("1649"));
        list.add(CC_688 = new DirectDialPrefixId("688"));
        list.add(CC_256 = new DirectDialPrefixId("256"));
        list.add(CC_380 = new DirectDialPrefixId("380"));
        list.add(CC_971 = new DirectDialPrefixId("971"));
        list.add(CC_44 = new DirectDialPrefixId("44"));
        list.add(CC_1 = new DirectDialPrefixId("1"));
        list.add(CC_598 = new DirectDialPrefixId("598"));
        list.add(CC_998 = new DirectDialPrefixId("998"));
        list.add(CC_678 = new DirectDialPrefixId("678"));
        list.add(CC_58 = new DirectDialPrefixId("58"));
        list.add(CC_84 = new DirectDialPrefixId("84"));
        list.add(CC_1284 = new DirectDialPrefixId("1284"));
        list.add(CC_1340 = new DirectDialPrefixId("1340"));
        list.add(CC_681 = new DirectDialPrefixId("681"));
        list.add(CC_967 = new DirectDialPrefixId("967"));
        list.add(CC_381 = new DirectDialPrefixId("381"));
        list.add(CC_263 = new DirectDialPrefixId("263"));
        list.add(CC_788 = new DirectDialPrefixId("788"));
        list.add(CC_1246 = new DirectDialPrefixId("1246"));
        list.add(CC_1204 = new DirectDialPrefixId("1204"));
        list.add(CC_1306 = new DirectDialPrefixId("1306"));
        list.add(CC_1403 = new DirectDialPrefixId("1403"));
        list.add(CC_1416 = new DirectDialPrefixId("1416"));
        list.add(CC_1418 = new DirectDialPrefixId("1418"));
        list.add(CC_1506 = new DirectDialPrefixId("1506"));
        list.add(CC_1514 = new DirectDialPrefixId("1514"));
        list.add(CC_1519 = new DirectDialPrefixId("1519"));
        list.add(CC_1600 = new DirectDialPrefixId("1600"));
        list.add(CC_1604 = new DirectDialPrefixId("1604"));
        list.add(CC_1613 = new DirectDialPrefixId("1613"));
        list.add(CC_1705 = new DirectDialPrefixId("1705"));
        list.add(CC_1709 = new DirectDialPrefixId("1709"));
        list.add(CC_1807 = new DirectDialPrefixId("1807"));
        list.add(CC_1819 = new DirectDialPrefixId("1819"));
        list.add(CC_1902 = new DirectDialPrefixId("1902"));
        list.add(CC_1905 = new DirectDialPrefixId("1905"));
        list.add(CC_1867 = new DirectDialPrefixId("1867"));
        list.add(CC_1250 = new DirectDialPrefixId("1250"));
        list.add(CC_1345 = new DirectDialPrefixId("1345"));
        list.add(CC_731 = new DirectDialPrefixId("731"));
        list.add(CC_732 = new DirectDialPrefixId("732"));
        list.add(CC_7330 = new DirectDialPrefixId("7330"));
        list.add(CC_7336 = new DirectDialPrefixId("7336"));
        list.add(CC_704 = new DirectDialPrefixId("704"));
        list.add(CC_703 = new DirectDialPrefixId("703"));
        list.add(CC_705 = new DirectDialPrefixId("705"));
        list.add(CC_706 = new DirectDialPrefixId("706"));
        list.add(CC_755 = new DirectDialPrefixId("755"));
        list.add(CC_8810 = new DirectDialPrefixId("8810"));
        list.add(CC_8811 = new DirectDialPrefixId("8811"));
        list.add(CC_8812 = new DirectDialPrefixId("8812"));
        list.add(CC_8813 = new DirectDialPrefixId("8813"));
        list.add(CC_8816 = new DirectDialPrefixId("8816"));
        list.add(CC_8817 = new DirectDialPrefixId("8817"));
        list.add(CC_8818 = new DirectDialPrefixId("8818"));
        list.add(CC_8819 = new DirectDialPrefixId("8819"));
        list.add(CC_871 = new DirectDialPrefixId("871"));
        list.add(CC_872 = new DirectDialPrefixId("872"));
        list.add(CC_873 = new DirectDialPrefixId("873"));
        list.add(CC_874 = new DirectDialPrefixId("874"));
        list.add(CC_870 = new DirectDialPrefixId("870"));
        list.add(CC_883 = new DirectDialPrefixId("883")); 
        // sort the list trivially using a tree map insertion
        {
            Comparator<String> sc = new Comparator<String>() {
                @Override
                public int compare(String prefix1, String prefix2) {
                    return DirectDialPrefixId.prefixComparison(prefix1, prefix2);
                }
            };
            TreeMap<String, DirectDialPrefixId> map = new TreeMap<String, DirectDialPrefixId>(sc);
            int maxCcLength = 0;
            for (DirectDialPrefixId ccid : list) {
                DirectDialPrefixId old = map.put(ccid.ddprefix, ccid);
                if (old != null) {
                    throw new IllegalStateException("The country code '" + ccid.ddprefix + "' already exists in the internal map");
                }
                maxCcLength = Math.max(ccid.ddprefix.length(), maxCcLength);
            }

            MAX_PREFIX_LENGTH = maxCcLength;
            MAP_BY_PREFIX = Collections.unmodifiableMap(map);
            // just use the values sorted by key for the LIST, but one has to create a copy of the returned list
            LIST = Collections.unmodifiableList(new ArrayList<DirectDialPrefixId>(map.values()));
        }
    }

    /**
     * The constructor, can only called by this class. The class defines the possible cases using the constructor and
     * that's it
     */

    private DirectDialPrefixId(String ddprefix) {
        this.ddprefix = ddprefix;
        if ("123456789".indexOf(ddprefix.charAt(0)) < 0) {
            throw new IllegalArgumentException("The country code '" + ddprefix + "' does not start with [1-9]");
        }
        for (int i = 1; i < ddprefix.length(); i++) {
            if ("0123456789".indexOf(ddprefix.charAt(i)) < 0) {
                throw new IllegalArgumentException("The country code '" + ddprefix + "' contains a non-digit");
            }
        }
    }

    /**
     * Get the direct dial prefix, as String
     */

    public String getDdPrefix() {
        return ddprefix;
    }

    /**
     * Transform this value into a string, same as getting the direct dial prefix
     */

    @Override
    public String toString() {
        return ddprefix;
    }

    /**
     * Comparison compares the underlying values. Their country code must be equal
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // quick guess
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DirectDialPrefixId)) {
            return false;
        }
        DirectDialPrefixId other = (DirectDialPrefixId) obj;
        return other.ddprefix.equals(this.ddprefix);
    }

    /**
     * Obtain a 'best fit' by giving a phone number. The id of the longest country code that matches is returned. May
     * either throw or return 'null' if not found
     */

    public static DirectDialPrefixId obtainUsingBestFit(String phoneNumber, boolean throwIfNotFound) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("The passed 'phone number' is (null)");
        } else {
            String prefix = phoneNumber.substring(0, Math.min(phoneNumber.length(), MAX_PREFIX_LENGTH));
            while (prefix.length() > 0) {
                DirectDialPrefixId ccId = MAP_BY_PREFIX.get(prefix);
                if (ccId != null) {
                    return ccId;
                } else {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
            }
            if (throwIfNotFound) {
                throw new IllegalArgumentException("The passed 'phone number' '" + phoneNumber + "' does not match any direct dial prefix");
            } else {
                return null;
            }
        }
    }

    /**
     * Obtain the CountryCodeId that exactly matches the given country-code prefix. May either throw or return 'null' if
     * not found
     */

    public static DirectDialPrefixId obtainUsingExactFit(String cc, boolean throwIfNotFound) {
        if (cc == null) {
            throw new IllegalArgumentException("The passed 'country code' is (null)");
        } else {
            DirectDialPrefixId ccId = MAP_BY_PREFIX.get(cc);
            if (ccId != null) {
                return ccId;
            } else if (throwIfNotFound) {
                throw new IllegalArgumentException("The passed 'country code' '" + cc + "' does not match any country code");
            } else {
                return null;
            }
        }
    }

    /**
     * Wanna hash this...
     */

    @Override
    public int hashCode() {
        return ddprefix.hashCode();
    }

    /**
     * Lexicographical comparison
     */

    public static int prefixComparison(String prefix1, String prefix2) {
        assert prefix1 != null;
        assert prefix2 != null;
        assert Pattern.matches("^\\d+$", prefix1);
        assert Pattern.matches("^\\d+$", prefix2);
        int p1_len = prefix1.length();
        int p2_len = prefix2.length();
        int minLength = Math.min(p1_len, p2_len);
        int i = 0;
        while (i < minLength) {
            char c1 = prefix1.charAt(i);
            char c2 = prefix2.charAt(i);
            if (c1 < c2) {
                return -1; // OUTTA HERE
            } else if (c1 > c2) {
                return +1; // OUTTA HERE
            } else {
                i++; // go on
            }
        }
        // sort the more precise (longer one) one to the front
        return p2_len - p1_len;
    }

    /**
     * Implementation of value-getting function
     */

    @Override
    public String getValue() {
        return ddprefix;
    }
}
