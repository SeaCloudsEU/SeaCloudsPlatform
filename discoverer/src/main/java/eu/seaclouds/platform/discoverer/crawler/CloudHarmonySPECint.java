/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package eu.seaclouds.platform.discoverer.crawler;

import java.util.HashMap;

public class CloudHarmonySPECint {

    private static HashMap<String, Integer> SPECint;

    static {
        HashMap<String, Integer> initializedMap = new HashMap<String, Integer>();

        initializedMap.put("Amazon EC2.c4.8xlarge", 795);
        initializedMap.put("Rackspace Cloud Servers.OnMetal I/O", 780);
        initializedMap.put("Amazon EC2.m4.10xlarge", 773);
        initializedMap.put("Rackspace Cloud Servers.Compute Optimized - 60GB/32 vCPU", 641);
        initializedMap.put("Amazon EC2.c3.8xlarge", 635);
        initializedMap.put("Amazon EC2.r3.8xlarge", 626);
        initializedMap.put("Amazon EC2.i2.8xlarge", 622);
        initializedMap.put("Rackspace Cloud Servers.Memory Optimized - 240GB/32 vCPU", 615);
        initializedMap.put("SoftLayer Cloud Servers.E5-2650 V2 - 64GB", 545);
        initializedMap.put("Google Compute Engine.n1-standard-32", 520);
        initializedMap.put("Microsoft Azure Virtual Machines.D14 - High Memory", 513);
        initializedMap.put("Microsoft Azure Virtual Machines.A9 - Network Optimized", 506);
        initializedMap.put("CenturyLink Cloud Servers.16cpu-32gb-hs", 496);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 120GB/32 vCPU", 493);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 90GB/24 vCPU", 467);
        initializedMap.put("Rackspace Cloud Servers.Compute Optimized - 30GB/16 vCPU", 457);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 90GB/24 vCPU", 446);
        initializedMap.put("Google Compute Engine.n1-standard-32", 446);
        initializedMap.put("CenturyLink Cloud Servers.16cpu-32gb", 440);
        initializedMap.put("Rackspace Cloud Servers.Memory Optimized - 120GB/16 vCPU", 438);
        initializedMap.put("DigitalOcean.64GB Droplet", 397);
        initializedMap.put("Rackspace Cloud Servers.60GB Performance 2", 394);
        initializedMap.put("Microsoft Azure Virtual Machines.D14 - High Memory", 389);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 60GB/16 vCPU", 384);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 381);
        initializedMap.put("Rackspace Cloud Servers.OnMetal Compute", 379);
        initializedMap.put("Microsoft Azure Virtual Machines.DS14 - Storage Optimized", 369);
        initializedMap.put("Amazon EC2.c4.4xlarge", 365);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 357);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 357);
        initializedMap.put("Linode Cloud Hosting.Linode 64GB", 349);
        initializedMap.put("Linode Cloud Hosting.Linode 96GB", 348);
        initializedMap.put("DigitalOcean.48GB Droplet", 330);
        initializedMap.put("Amazon EC2.m4.4xlarge", 327);
        initializedMap.put("DigitalOcean.64GB Droplet", 326);
        initializedMap.put("Rackspace Cloud Servers.OnMetal Memory", 323);
        initializedMap.put("Amazon EC2.c3.4xlarge", 323);
        initializedMap.put("Amazon EC2.r3.4xlarge", 315);
        initializedMap.put("Amazon EC2.i2.4xlarge", 315);
        initializedMap.put("Linode Cloud Hosting.Linode 48GB", 310);
        initializedMap.put("Google Compute Engine.n1-highmem-16", 302);
        initializedMap.put("Google Compute Engine.n1-highcpu-16", 292);
        initializedMap.put("Google Compute Engine.n1-standard-16", 292);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 288);
        initializedMap.put("Google Compute Engine.n1-highmem-16", 287);
        initializedMap.put("DigitalOcean.64GB Droplet", 284);
        initializedMap.put("Google Compute Engine.n1-highcpu-16", 280);
        initializedMap.put("CenturyLink Cloud Servers.8cpu-16gb", 278);
        initializedMap.put("DigitalOcean.64GB Droplet", 278);
        initializedMap.put("CenturyLink Cloud Servers.8cpu-16gb-hs", 277);
        initializedMap.put("Google Compute Engine.n1-standard-16", 274);
        initializedMap.put("Google Compute Engine.n1-highcpu-16", 272);
        initializedMap.put("Google Compute Engine.n1-highcpu-16", 269);
        initializedMap.put("Linode Cloud Hosting.Linode 32GB", 269);
        initializedMap.put("SoftLayer Cloud Servers.8core-16gb", 258);
        initializedMap.put("Rackspace Cloud Servers.Compute Optimized - 15GB/8 vCPU", 255);
        initializedMap.put("Microsoft Azure Virtual Machines.A8 - Network Optimized", 255);
        initializedMap.put("Rackspace Cloud Servers.Memory Optimized - 60GB/8 vCPU", 253);
        initializedMap.put("DigitalOcean.64GB Droplet", 252);
        initializedMap.put("DigitalOcean.48GB Droplet", 252);
        initializedMap.put("DigitalOcean.48GB Droplet", 249);
        initializedMap.put("CenturyLink Cloud Servers.8cpu-16gb", 234);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 30GB/8 vCPU", 226);
        initializedMap.put("Linode Cloud Hosting.Linode 16GB", 225);
        initializedMap.put("Rackspace Cloud Servers.30GB Performance 2", 223);
        initializedMap.put("DigitalOcean.48GB Droplet", 218);
        initializedMap.put("Amazon EC2.hs1.8xlarge", 218);
        initializedMap.put("DigitalOcean.32GB Droplet", 213);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 213);
        initializedMap.put("SoftLayer Cloud Servers.8core-16gb", 210);
        initializedMap.put("DigitalOcean.32GB Droplet", 210);
        initializedMap.put("Rackspace Cloud Servers.8GB Performance 1", 208);
        initializedMap.put("DigitalOcean.32GB Droplet", 205);
        initializedMap.put("Amazon EC2.c4.2xlarge", 200);
        initializedMap.put("Rackspace Cloud Servers.General Purpose - 8GB/8 vCPU", 199);
        initializedMap.put("Microsoft Azure Virtual Machines.D4 - General Purpose", 197);
        initializedMap.put("SoftLayer Cloud Servers.E3-1270 - 32GB", 195);
        initializedMap.put("Microsoft Azure Virtual Machines.D13 - High Memory", 194);
        initializedMap.put("Microsoft Azure Virtual Machines.DS13 - Storage Optimized", 190);
        initializedMap.put("SoftLayer Cloud Servers.8core-16gb", 189);
        initializedMap.put("Linode Cloud Hosting.Linode 16GB", 189);
        initializedMap.put("Linode Cloud Hosting.Linode 8GB", 177);
        initializedMap.put("Amazon EC2.c3.2xlarge", 173);
        initializedMap.put("Amazon EC2.r3.2xlarge", 171);
        initializedMap.put("Amazon EC2.m3.2xlarge", 171);
        initializedMap.put("Amazon EC2.i2.2xlarge", 170);
        initializedMap.put("Amazon EC2.m4.2xlarge", 167);
        initializedMap.put("Linode Cloud Hosting.Linode 8GB", 166);
        initializedMap.put("DigitalOcean.16GB Droplet", 166);
        initializedMap.put("Google Compute Engine.n1-highcpu-8", 165);
        initializedMap.put("Google Compute Engine.n1-highmem-8", 163);
        initializedMap.put("Amazon EC2.g2.2xlarge", 162);
        initializedMap.put("SoftLayer Cloud Servers.8core-16gb", 161);
        initializedMap.put("DigitalOcean.16GB Droplet", 159);
        initializedMap.put("Google Compute Engine.n1-highmem-8", 158);
        initializedMap.put("Google Compute Engine.n1-standard-8", 158);
        initializedMap.put("Google Compute Engine.n1-standard-8", 158);
        initializedMap.put("DigitalOcean.16GB Droplet", 153);
        initializedMap.put("Google Compute Engine.n1-highcpu-8", 150);
        initializedMap.put("SoftLayer Cloud Servers.16core-32gb", 148);
        initializedMap.put("CenturyLink Cloud Servers.4cpu-8gb", 147);
        initializedMap.put("DigitalOcean.16GB Droplet", 140);
        initializedMap.put("CenturyLink Cloud Servers.4cpu-8gb", 139);
        initializedMap.put("CenturyLink Cloud Servers.4cpu-8gb", 136);
        initializedMap.put("Rackspace Cloud Servers.Compute Optimized - 7.5GB/4 vCPU", 134);
        initializedMap.put("Rackspace Cloud Servers.Memory Optimized - 30GB/4 vCPU", 134);
        initializedMap.put("SoftLayer Cloud Servers.4core-8gb", 127);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 15GB/4 vCPU", 126);
        initializedMap.put("Microsoft Azure Virtual Machines.A7 - Memory Intensive - Standard", 120);
        initializedMap.put("Rackspace Cloud Servers.I/O Optimized - 15GB/4 vCPU", 120);
        initializedMap.put("CenturyLink Cloud Servers.4cpu-8gb-hs", 118);
        initializedMap.put("Linode Cloud Hosting.Linode 4GB", 117);
        initializedMap.put("Rackspace Cloud Servers.15GB Performance 2", 115);
        initializedMap.put("Rackspace Cloud Servers.General Purpose - 4GB/4 vCPU", 114);
        initializedMap.put("SoftLayer Cloud Servers.4core-8gb", 111);
        initializedMap.put("Rackspace Cloud Servers.General Purpose - 4GB/4 vCPU", 108);
        initializedMap.put("Rackspace Cloud Servers.4GB Performance 1", 108);
        initializedMap.put("Rackspace Cloud Servers.4GB Performance 1", 107);
        initializedMap.put("Amazon EC2.c4.xlarge", 104);
        initializedMap.put("DigitalOcean.8GB Droplet", 104);
        initializedMap.put("Microsoft Azure Virtual Machines.DS12 - Storage Optimized", 102);
        initializedMap.put("DigitalOcean.8GB Droplet", 102);
        initializedMap.put("Microsoft Azure Virtual Machines.D12 - High Memory", 100);
        initializedMap.put("Microsoft Azure Virtual Machines.D3 - General Purpose", 100);
        initializedMap.put("SoftLayer Cloud Servers.4core-8gb", 98);
        initializedMap.put("Google Compute Engine.n1-highmem-4", 96);
        initializedMap.put("DigitalOcean.8GB Droplet", 94);
        initializedMap.put("Amazon EC2.c3.xlarge", 92);
        initializedMap.put("SoftLayer Cloud Servers.4core-8gb", 91);
        initializedMap.put("Amazon EC2.d2.xlarge", 91);
        initializedMap.put("Amazon EC2.m4.xlarge", 90);
        initializedMap.put("Amazon EC2.i2.xlarge", 90);
        initializedMap.put("Amazon EC2.m3.xlarge", 90);
        initializedMap.put("DigitalOcean.8GB Droplet", 89);
        initializedMap.put("Amazon EC2.r3.xlarge", 89);
        initializedMap.put("SoftLayer Cloud Servers.4core-8gb", 89);
        initializedMap.put("Liquid Web Storm Servers.2GB Storm SSD", 86);
        initializedMap.put("Google Compute Engine.n1-highcpu-4", 86);
        initializedMap.put("Google Compute Engine.n1-highmem-4", 86);
        initializedMap.put("Google Compute Engine.n1-highcpu-4", 82);
        initializedMap.put("Google Compute Engine.n1-standard-4", 82);
        initializedMap.put("Google Compute Engine.n1-standard-4", 82);
        initializedMap.put("Google Compute Engine.n1-highcpu-4", 78);
        initializedMap.put("CenturyLink Cloud Servers.2cpu-4gb", 76);
        initializedMap.put("Rackspace Cloud Servers.Compute Optimized - 3.75GB/2 vCPU", 74);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 73);
        initializedMap.put("CenturyLink Cloud Servers.2cpu-4gb-hs", 73);
        initializedMap.put("Google Compute Engine.n1-highcpu-4", 70);
        initializedMap.put("Rackspace Cloud Servers.Memory Optimized - 15GB/2 vCPU", 70);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 66);
        initializedMap.put("Rackspace Cloud Servers.2GB Performance 1", 61);
        initializedMap.put("Linode Cloud Hosting.Linode 2GB", 60);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 60);
        initializedMap.put("Rackspace Cloud Servers.General Purpose - 2GB/2 vCPU", 57);
        initializedMap.put("Rackspace Cloud Servers.2GB Performance 1", 57);
        initializedMap.put("DigitalOcean.2GB Droplet", 55);
        initializedMap.put("Microsoft Azure Virtual Machines.D11 - High Memory", 54);
        initializedMap.put("Amazon EC2.c4.large", 54);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 54);
        initializedMap.put("DigitalOcean.4GB Droplet", 53);
        initializedMap.put("Microsoft Azure Virtual Machines.D2 - General Purpose", 53);
        initializedMap.put("Microsoft Azure Virtual Machines.A6 - Memory Intensive - Standard", 53);
        initializedMap.put("Microsoft Azure Virtual Machines.DS11 - Storage Optimized", 52);
        initializedMap.put("DigitalOcean.2GB Droplet", 48);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 47);
        initializedMap.put("Amazon EC2.m4.large", 47);
        initializedMap.put("Amazon EC2.c3.large", 47);
        initializedMap.put("Amazon EC2.r3.large", 46);
        initializedMap.put("DigitalOcean.2GB Droplet", 46);
        initializedMap.put("Google Compute Engine.n1-highcpu-2", 46);
        initializedMap.put("Amazon EC2.m3.large", 46);
        initializedMap.put("SoftLayer Cloud Servers.2core-4gb", 45);
        initializedMap.put("DigitalOcean.4GB Droplet", 44);
        initializedMap.put("DigitalOcean.4GB Droplet", 43);
        initializedMap.put("Google Compute Engine.n1-standard-2", 43);
        initializedMap.put("Google Compute Engine.n1-highcpu-2", 42);
        initializedMap.put("Amazon EC2.t2.medium", 42);
        initializedMap.put("Google Compute Engine.n1-highmem-2", 42);
        initializedMap.put("DigitalOcean.2GB Droplet", 41);
        initializedMap.put("Google Compute Engine.n1-standard-2", 41);
        initializedMap.put("Amazon EC2.t2.micro", 40);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 40);
        initializedMap.put("Google Compute Engine.n1-standard-1", 34);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 34);
        initializedMap.put("Google Compute Engine.n1-standard-1", 33);
        initializedMap.put("Rackspace Cloud Servers.General Purpose - 1GB/1 vCPU", 32);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 31);
        initializedMap.put("Linode Cloud Hosting.Linode 1GB", 30);
        initializedMap.put("DigitalOcean.512MB Droplet", 30);
        initializedMap.put("DigitalOcean.1GB Droplet", 29);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 29);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 28);
        initializedMap.put("Microsoft Azure Virtual Machines.D1 - General Purpose", 27);
        initializedMap.put("DigitalOcean.1GB Droplet", 26);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 25);
        initializedMap.put("DigitalOcean.1GB Droplet", 25);
        initializedMap.put("SoftLayer Cloud Servers.1core-2gb", 24);
        initializedMap.put("DigitalOcean.1GB Droplet", 23);
        initializedMap.put("Amazon EC2.t2.small", 21);
        initializedMap.put("DigitalOcean.1GB Droplet", 20);
        initializedMap.put("Amazon EC2.m3.medium", 19);
        initializedMap.put("Google Compute Engine.g1-small", 17);
        initializedMap.put("Google Compute Engine.g1-small", 15);
        initializedMap.put("Google Compute Engine.f1-micro", 8);

        SPECint = initializedMap;
    }

    /**
     * Gets the SPECint of the specified instance of the specified offerings provider
     *
     * @param providerName the name of the offerings provider
     * @param instanceType istance type as specified in the CloudHarmony API
     * @return SPECint of the specified instance
     */
    public static Integer getSPECint(String providerName, String instanceType) {
        String key = providerName + "." + instanceType;
        return SPECint.get(key);
    }

}
