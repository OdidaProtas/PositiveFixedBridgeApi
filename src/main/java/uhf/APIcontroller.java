package uhf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class APIcontroller {
    public static int connectionstatus = 1;
    public static int Port;
    public static byte[] comAddr = new byte[1];
    public static int[] PortHandle = new int[1];
    public static byte[] Ant = new byte[1];


    public static List<String> Tags = new ArrayList<String>();


    @RequestMapping("/")
    public String index() {
        return "The applications is set up!";
    }

//    @RequestMapping("/connect-reader")
//    public String connectReader() {
//        Port = 1;
//        comAddr[0] = (byte) 255;
//        byte baud = 5;
//        int[] PortHandle = new int[1];
//        try {
//            int result = MainApp.reader.OpenComPort(7, comAddr, baud, PortHandle);
//            if (result == 0) {
//                connectionstatus = 0;
//                return "The reader has been connected successfully with code" + result;
//            } else {
//                connectionstatus = result;
//                return "An error occurred. Unplug the device and try again";
//            }
//        } catch (Exception e) {
//            return e.toString();
//        }
//    }

    @RequestMapping("/scan")
    public String getReaderInfo() {
        byte baud = 5;
//        int result = MainApp.reader.OpenComPort(7, comAddr, baud, PortHandle);
        if (connectionstatus == 0)
            MainApp.reader.CloseSpecComPort(PortHandle[0]);

        int result2 = MainApp.reader.OpenComPort(7, comAddr, baud, PortHandle);
        Tags.clear();
        connectionstatus = 0;
        if (result2 == 0) {
            byte QValue = 4;
            byte Session = 0;
            byte MaskMem = 2;
            byte[] MaskAdr = new byte[2];
            byte MaskLen = 0;
            byte[] MaskData = new byte[256];
            byte MaskFlag = 0;
            byte AdrTID = 0;
            byte LenTID = 6;
            byte TIDFlag = 1;//¶ÁTIDµÄÇ°6¸ö×Ö
            byte Target = 0;
            byte InAnt = (byte) 0x80;
            byte Scantime = 10;
            byte FastFlag = 0;
            byte[] pEPCList = new byte[20000];
            int[] Totallen = new int[1];
            int[] CardNum = new int[1];
            result2 = MainApp.reader.Inventory_G2(comAddr, QValue, Session, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag,
                    AdrTID, LenTID, TIDFlag, Target, InAnt, Scantime, FastFlag, pEPCList, Ant, Totallen,
                    CardNum, PortHandle[0]);
            if (CardNum[0] > 0) {
                int m = 0;
                for (int index = 0; index < CardNum[0]; index++) {
                    int epclen = pEPCList[m++] & 255;
                    String EPCstr = "";
                    byte[] epc = new byte[epclen];
                    for (int n = 0; n < epclen; n++) {
                        byte bbt = pEPCList[m++];
                        epc[n] = bbt;
                        String hex = Integer.toHexString(bbt & 255);
                        if (hex.length() == 1) {
                            hex = "0" + hex;
                        }
                        EPCstr += hex;
                    }
                    int rssi = pEPCList[m++];
                    //¸ù¾ÝTIDºÅÐ´Êý¾Ý
                    byte ENum = (byte) 255;//ÑÚÂë
                    byte Mem = 1;//¶ÁEPC
                    byte WordPtr = 2;//´ÓµÚ2×Ö¿ªÊ¼
                    byte Num = 6;//¶Á6¸ö×Ö
                    byte[] Password = new byte[4];
                    MaskMem = 2;//TIDÑÚÂë
                    MaskAdr[0] = 0;
                    MaskAdr[1] = 0;
                    MaskLen = 96;
                    int p = 0;
                    System.arraycopy(epc, 0, MaskData, 0, 96 / 8);
                    byte[] Data = new byte[Num * 2];
                    int[] Errorcode = new int[1];
                    byte WNum = 7;
                    byte[] Wdt = new byte[WNum * 2];
                    Wdt[0] = 0x30;
                    Wdt[1] = 0x00;//µÚÒ»¸ö×ÖPC³¤¶È
                    Wdt[2] = (byte) 0xE2;
                    Wdt[3] = 0x00;
                    Wdt[4] = 0x12;
                    Wdt[5] = 0x34;
                    Wdt[6] = 0x56;
                    Wdt[7] = 0x78;
                    Wdt[8] = 0x12;
                    Wdt[9] = 0x34;
                    Wdt[10] = 0x56;
                    Wdt[11] = 0x78;
                    Wdt[12] = 0x12;
                    Wdt[13] = 0x34;
                    WordPtr = 1;
                    result2 = MainApp.reader.WriteData_G2(comAddr, epc, WNum, ENum, Mem, WordPtr, Wdt, Password,
                            MaskMem, MaskAdr, MaskLen, MaskData, Errorcode, PortHandle[0]);
                    System.out.println("Write Data Result " + result2);
                    WordPtr = 2;
                    result2 = MainApp.reader.ReadData_G2(comAddr, epc, ENum, Mem, WordPtr, Num, Password,
                            MaskMem, MaskAdr, MaskLen, MaskData, Data, Errorcode, PortHandle[0]);
                    System.out.println("Read Data G2 " + result2);
                    if (result2 == 0) {
                        String Memdata = "";
                        for (p = 0; p < Num * 2; p++) {
                            byte bbt = Data[p];
                            String hex = Integer.toHexString(bbt & 255);
                            if (hex.length() == 1) {
                                hex = "0" + hex;
                            }
                            Memdata += hex;
                        }
                        System.out.println(Memdata + " mem \n");
                        Tags.add(Memdata);
                    }
                }
            }
            return Tags.toString();
        } else {
            return "An error occured";
        }
    }
}
//    @RequestMapping("/get-tags")
//    public String getTags() {
//        if (MainApp.result == 0) {
//            System.out.println(MainApp.Tags);
//            return MainApp.Tags.toString();
//        } else {
//            return "No Tags Available";
//        }
//    }
//
//    @RequestMapping("/start-scanning")
//    public String startScanning() {
//        return "Scanning";
//    }

//}




















