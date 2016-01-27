package com.dianping.swallow.producer.impl;

import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.domain.ConnectInfo;
import com.dianping.pigeon.remoting.netty.invoker.NettyClient;

import java.util.*;

/**
 * @author qi.yin
 *         2016/01/26  下午5:38.
 */
public class SwallowPigeonLoadBalancePressureTest {

    private String serviceName = "com.dianping.swallow.common.internal.producer.ProducerSwallowService";

    private List<Client> clients;

    private String[] producerIps = new String[producerCount];

    private static final int clientCount = 20;

    private static final int producerCount = 5;

    protected Random random = new Random();

    public Set<String> getProducerIpSet() {
        return producerIpSet;
    }

    public void setProducerIpSet(Set<String> producerIpSet) {
        this.producerIpSet = producerIpSet;
    }

    private Set<String> producerIpSet = new HashSet<String>(producerIps.length);

    public void beforeTest() {
        clients = new ArrayList<Client>();

        for (int i = 0; i < clientCount; i++) {
            clients.add(new NettyClient(new ConnectInfo(serviceName, "192.168.21." + i, 4000, 1)));
        }

        for (int i = 0; i < producerCount - 1; i++) {
            producerIps[i] = "192.168.21." + (i);
        }
        producerIps[producerCount - 1] = "192.168.21." + (clientCount);
        producerIpSet.addAll(Arrays.asList(producerIps));
    }

    public Client selectClient2(List<Client> clients, String[] producerIps) {
        Client selectedClient = null;

        if (clients.size() == 1) {
            return clients.get(0);
        }

        int maxCount = producerIps.length;
        int count = 0;
        while (selectedClient == null && count < maxCount) {
            String selectedIp;

            if (producerIps.length == 1) {
                selectedIp = producerIps[0];
            } else {
                selectedIp = producerIps[random.nextInt(producerIps.length)];
            }

            for (Client client : clients) {
                if (client.getHost().equals(selectedIp)) {
                    selectedClient = client;
                }
            }
            count++;

        }

        if (selectedClient == null) {
            clients.get(random.nextInt(clients.size()));
        }

        return selectedClient;
    }

    public List<Client> selectClients1(List<Client> clients, String[] producerIps) {
        //Set<String> producerIpSet = new HashSet<String>(Arrays.asList(producerIps));
        List<Client> copyClients = new ArrayList<Client>(producerIpSet.size());
        if (clients != null) {
            Iterator<Client> it = clients.iterator();

            while (it.hasNext()) {
                Client client = it.next();
                if (producerIpSet.contains(client.getHost())) {
                    copyClients.add(client);
                }
            }

            if (!copyClients.isEmpty()) {
                return copyClients;
            }

        }

        return clients;
    }

    public List<Client> selectClients0(List<Client> clients, String[] producerIps_) {

        List<Client> copyClients = new ArrayList<Client>(producerIps_.length);
        String[] producerIps = producerIps_;

        if (clients != null) {
            Iterator<Client> it = clients.iterator();

            while (it.hasNext()) {
                Client client = it.next();

                for (String ip : producerIps) {
                    if (client.getHost().equals(ip)) {
                        copyClients.add(client);
                    }
                }
            }

            if (!copyClients.isEmpty()) {
                return copyClients;
            }

        }

        return clients;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public String[] getProducerIps() {
        return producerIps;
    }

    public void setProducerIps(String[] producerIps) {
        this.producerIps = producerIps;
    }


    public static void main(String[] args) {
        SwallowPigeonLoadBalancePressureTest pressureTest = new SwallowPigeonLoadBalancePressureTest();
        pressureTest.beforeTest();


        System.out.println("Start--Pressure--selectClients0");
        long startTime0 = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            pressureTest.selectClients0(pressureTest.getClients(), pressureTest.getProducerIps());
//            List<Client> selectedClients = pressureTest.selectClients0(pressureTest.getClients(), pressureTest.getProducerIps());
//            for(Client client: selectedClients){
//                System.out.println(client.getHost());
//            }
        }

        long endTime0 = System.currentTimeMillis();
        System.out.println("End--Pressure--selectClients0 ----" + (endTime0 - startTime0));

        System.out.println("Start--Pressure--selectClients1");
        long startTime1 = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            pressureTest.selectClients1(pressureTest.getClients(), pressureTest.getProducerIps());
//            List<Client> selectedClients =pressureTest.selectClients1(pressureTest.getClients(), pressureTest.getProducerIps());
//            for(Client client: selectedClients){
//                System.out.println(client.getHost());
//            }
        }

        long endTime1 = System.currentTimeMillis();
        System.out.println("End--Pressure--selectClients1---" + (endTime1 - startTime1));

        System.out.println("Start--Pressure--selectClients2");
        long startTime2 = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            pressureTest.selectClient2(pressureTest.getClients(), pressureTest.getProducerIps());
        }

        long endTime2 = System.currentTimeMillis();
        System.out.println("End--Pressure--selectClients2 ----" + (endTime2 - startTime2));

    }
}
