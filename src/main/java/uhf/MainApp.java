package uhf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MainApp {


    public static com.rfid.uhf.Device reader;
    public static int result;

//    public static List<String> Tags = new ArrayList<String>();


    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
        System.loadLibrary("com_rfid_uhf_Device");
        reader = new com.rfid.uhf.Device();


//        int Port = 1;
//
//        byte[] comAddr = new byte[1];
//        comAddr[0] = (byte) 255;
//        byte baud = 5;
//        int[] PortHandle = new int[1];
//
//        result = reader.OpenComPort(7, comAddr, baud, PortHandle);
//        System.out.println("Opening Port Result: " + result);
//
//        if (result == 0) {
//            byte[] versionInfo = new byte[2];
//            byte[] readerType = new byte[1];
//            byte[] trType = new byte[1];
//            byte[] dmaxfre = new byte[1];
//            byte[] dminfre = new byte[1];
//            byte[] powerdBm = new byte[1];
//            byte[] InventoryScanTime = new byte[1];
//            byte[] Ant = new byte[1];
//            byte[] BeepEn = new byte[1];
//            byte[] OutputRep = new byte[1];
//            byte[] CheckAnt = new byte[1];
//            result = reader.GetReaderInformation(comAddr, versionInfo, readerType, trType, dmaxfre, dminfre, powerdBm, InventoryScanTime,
//                    Ant, BeepEn, OutputRep, CheckAnt, PortHandle[0]);
//            System.out.println("Get reader info" + result);
//            byte ComAdrData = 0;
//            result = reader.SetAddress(comAddr, ComAdrData, PortHandle[0]);
//            System.out.println("Set address" + result);
//
//
//            byte QValue = 4;
//            byte Session = 0;
//            byte MaskMem = 2;
//            byte[] MaskAdr = new byte[2];
//            byte MaskLen = 0;
//            byte[] MaskData = new byte[256];
//            byte MaskFlag = 0;
//            byte AdrTID = 0;
//            byte LenTID = 6;
//            byte TIDFlag = 1;//¶ÁTIDµÄÇ°6¸ö×Ö
//            byte Target = 0;
//            byte InAnt = (byte) 0x80;
//            byte Scantime = 10;
//            byte FastFlag = 0;
//            byte[] pEPCList = new byte[20000];
//            int[] Totallen = new int[1];
//            int[] CardNum = new int[1];
//            result = reader.Inventory_G2(comAddr, QValue, Session, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag,
//                    AdrTID, LenTID, TIDFlag, Target, InAnt, Scantime, FastFlag, pEPCList, Ant, Totallen,
//                    CardNum, PortHandle[0]);
//            System.out.println("G2 Inventory " + result);
//            if (CardNum[0] > 0) {
//                System.out.println("CardNum " + CardNum[0]);
//                int m = 0;
//                for (int index = 0; index < CardNum[0]; index++) {
//                    int epclen = pEPCList[m++] & 255;
//                    String EPCstr = "";
//                    byte[] epc = new byte[epclen];
//                    for (int n = 0; n < epclen; n++) {
//                        byte bbt = pEPCList[m++];
//                        epc[n] = bbt;
//                        String hex = Integer.toHexString(bbt & 255);
//                        if (hex.length() == 1) {
//                            hex = "0" + hex;
//                        }
//                        EPCstr += hex;
//                    }
//                    int rssi = pEPCList[m++];
//                    System.out.println(EPCstr.toUpperCase());
//                    //¸ù¾ÝTIDºÅÐ´Êý¾Ý
//                    byte ENum = (byte) 255;//ÑÚÂë
//                    byte Mem = 1;//¶ÁEPC
//                    byte WordPtr = 2;//´ÓµÚ2×Ö¿ªÊ¼
//                    byte Num = 6;//¶Á6¸ö×Ö
//                    byte[] Password = new byte[4];
//                    MaskMem = 2;//TIDÑÚÂë
//                    MaskAdr[0] = 0;
//                    MaskAdr[1] = 0;
//                    MaskLen = 96;
//                    int p = 0;
//                    System.arraycopy(epc, 0, MaskData, 0, 96 / 8);
//                    byte[] Data = new byte[Num * 2];
//                    int[] Errorcode = new int[1];
//                    byte WNum = 7;
//                    byte[] Wdt = new byte[WNum * 2];
//                    Wdt[0] = 0x30;
//                    Wdt[1] = 0x00;//µÚÒ»¸ö×ÖPC³¤¶È
//                    Wdt[2] = (byte) 0xE2;
//                    Wdt[3] = 0x00;
//                    Wdt[4] = 0x12;
//                    Wdt[5] = 0x34;
//                    Wdt[6] = 0x56;
//                    Wdt[7] = 0x78;
//                    Wdt[8] = 0x12;
//                    Wdt[9] = 0x34;
//                    Wdt[10] = 0x56;
//                    Wdt[11] = 0x78;
//                    Wdt[12] = 0x12;
//                    Wdt[13] = 0x34;
//                    WordPtr = 1;
//                    result = reader.WriteData_G2(comAddr, epc, WNum, ENum, Mem, WordPtr, Wdt, Password,
//                            MaskMem, MaskAdr, MaskLen, MaskData, Errorcode, PortHandle[0]);
//                    System.out.println("Write Data Result " + result);
//                    WordPtr = 2;
//                    result = reader.ReadData_G2(comAddr, epc, ENum, Mem, WordPtr, Num, Password,
//                            MaskMem, MaskAdr, MaskLen, MaskData, Data, Errorcode, PortHandle[0]);
//                    System.out.println("Read Data G2 " + result);
//                    if (result == 0) {
//                        String Memdata = "";
//                        for (p = 0; p < Num * 2; p++) {
//                            byte bbt = Data[p];
//                            String hex = Integer.toHexString(bbt & 255);
//                            if (hex.length() == 1) {
//                                hex = "0" + hex;
//                            }
//                            Memdata += hex;
//                        }
//                        System.out.println(Memdata + " mem \n");
//                        Tags.add(Memdata);
//
//                    }
//                }
//            }
//        }
//        reader.CloseSpecComPort(PortHandle[0]);
    }
}
