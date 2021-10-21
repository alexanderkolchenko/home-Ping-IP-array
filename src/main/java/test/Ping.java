package test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Выполняет одновременный запрос по каждому IP из массива, формирует список результатов пинга
 * вычисляет самый маленький пинг и отправляет все результаты в Main для вывода на экран
 */
public class Ping {
    /**
     * список IP
     */
    public static String[] ipAddressList = {
            "35.156.63.252",
            "52.28.63.252",
            "52.29.63.252",
            "52.57.255.254",
            "52.58.63.252",
            "54.93.32.2",
            "54.93.162.162",
            "52.57.67.93",
            "52.28.34.137",
            "52.57.67.93",
            "52.57.169.116",
            "52.59.68.3",
            "52.57.168.131",
            "35.157.61.251",
            "52.57.241.171",
            "52.29.125.80",
            "52.58.95.114",
            "35.157.61.4"};

    /**
     * список результатов запроса, ip : пинг в мс
     * который отправляется в Main для одраоотки и вывода на экран
     */
    public static ConcurrentHashMap<String, Integer> listOfPingResult = new ConcurrentHashMap<String, Integer>();

    /**
     * перебирает список IP адресов и по каждому отправляет запрос отдельным потоком
     *
     * @return список результатов запроса, ip : пинг в мс
     */
    public static ConcurrentHashMap<String, Integer> getPingList() throws InterruptedException {

        Thread threadForOneIP = null;

        //перебод адресов из начального массива и запуск отдельного запроса по каждому
        for (String s : ipAddressList) {
            String IpFromList = s;
            String pingForRequest = "ping " + IpFromList;
            threadForOneIP = new Thread(new Request(pingForRequest, IpFromList));
            threadForOneIP.start();
        }
        threadForOneIP.join();

        return listOfPingResult;
    }

    /**
     * находит ip адрес с минимальным пингом из результов запроса listOfPingResult
     *
     * @param listOfPingResult список результатов запроса, ip : пинг в мс
     * @return ip адрес с минимальным пингом
     */
    public static AtomicReference getMinValueOfPing(ConcurrentHashMap<String, Integer> listOfPingResult) {

        List<Integer> l = new ArrayList<Integer>(listOfPingResult.values());
        int min = Collections.min(l);
        AtomicReference<String> minPing = new AtomicReference<>("");
        listOfPingResult.forEach((k, v) -> {
            if (v == min) {
                minPing.set("best ping " + k + ": " + v + " ms");
            }
        });
        return minPing;
    }

    /**
     * Формирует запрос для отдельного IP, имитирует команду ping в CMD
     */

    static class Request implements Runnable {

        String ping;
        String ip;

        public Request(String ping, String ip) {
            this.ping = ping;
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(ping);
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String response;
                //ответ возрачает кучу строк с разной информацией
                //через substring находим нужные значения
                while ((response = reader.readLine()) != null) {
                    //кодировка командной строки не совпадает консолью идеи,
                    //эти символы означают нет ответа от сервера
                    if (response.equals("�ॢ�襭 ���ࢠ� �������� ��� �����.")) {
                        //999 - вместо строки, значит нет ответа
                        System.out.println(ip + ": no response");
                        Ping.listOfPingResult.put(ip, 999);
                        break;
                    }
                    if (response.length() > 50) {
                        int pingValue = Integer.valueOf(response.substring(48, 51).trim());
                        Ping.listOfPingResult.put(ip, pingValue);
                        System.out.println(ip + ": " + pingValue + " ms");
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}



