# SeaClouds

## Deploy SeaClouds
A deployment of SeaClouds can be launched using Apache Brooklyn. We currently support deployments against Bring Your 
Own Nodes (BYON) and to all the IaaS provider supported by [Apache jclouds](http://jclouds.org).


# Deploy SeaClouds platform

Make sure you have [Apache Maven](https://maven.apache.org/) v.3.5.5+

First of all you need to build the SeaCloudsPlatform project with:

```bash
mvn clean install
```

## Launching SeaClouds on the cloud

```bash
cd usage/installer/target/seaclouds-installer-dist/seaclouds-installer
nohup ./start.sh --app blueprints/seaclouds.yaml --location <location> &
```

## Launching SeaClouds on BYON

Make sure you have [Vagrant](https://www.vagrantup.com/), [Virtual Box](https://www.virtualbox.org/)

```bash
cd usage/installer/target/seaclouds-installer-dist/seaclouds-installer
pushd .
cd byon
vagrant up 
popd
nohup ./start.sh &
tail -f nohup.out
```
This spins up a virtual environment, made up of 2 VMs, that is accessible at `192.168.100.10` and `192.168.100.11`.

Finally, copy and paste [seaclouds blueprint](./src/main/assembly/files/blueprints/seaclouds-on-byon.yaml) to deploy the SeaClouds platform on the 2 VMs.

If you prefer you can also launch the platform deployment from CLI

```bash
nohup ./start.sh --app blueprints/seaclouds-on-byon.yaml &
tail -f nohup.out
```

### SeaClouds release 0.7.0-M19

A detailed description of [0.7.0-M19](https://github.com/SeaCloudsEU/SeaCloudsPlatform/releases/tag/0.7.0-M19) SeaClouds release including: 
- SeaClouds components and their interactions
- A guide to get an install SeaClouds Platform 
- An example of how to use SeaClouds Platform and exploit its capabilities and the capabilies of each of its components

can be found in the [Integrated Platform deliverable](https://drive.google.com/file/d/0B3naRHlVBGTEdmYySFVWSGdIYzA/view?usp=sharing).

### Troubleshooting

When deploying SeaClouds platform an [Apache Brooklyn](http://brooklyn.io) instance will be started on your 
workstation, accessible at `http://localhost:8081` by default. Please double-check in nohup.out the correct url.

You may need to update the `privateKeyFile` property in the blueprint to the actual path.
By default, it points to `../seaclouds-installer/byon/seaclouds_id_rsa`  but YMMV.

For more information, please visit [Apache Brooklyn](https://brooklyn.incubator.apache.org/download/index.html)
