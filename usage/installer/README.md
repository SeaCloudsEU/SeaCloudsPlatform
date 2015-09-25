# SeaClouds

## Deploy SeaClouds
A deployment of SeaClouds can be launched using Apache Brooklyn. We currently support deployments against Bring Your
Own Nodes (BYON) and to all the IaaS provider supported by [Apache jclouds](http://jclouds.org).

# Launching SeaClouds platform

- download from `https://oss.sonatype.org/content/repositories/snapshots/eu/seaclouds-project/installer/0.8.0-SNAPSHOT/`
the latest `installer-0.8.0-*-dist.tar.gz`
- unpack it: `tar xzf installer-0.8.0-*-dist.tar.gz`

## Deploying SeaClouds on the cloud

```bash
cd seaclouds-installer
```

Open `blueprints/seaclouds.yaml` and add a location to the blueprint, similar to:

```
location:
  jclouds:aws-ec2:eu-west-1:
    identity: ABCDEFGHIJKLMNOPQRST
    credential: s3cr3tsq1rr3ls3cr3tsq1rr3ls3cr3tsq1rr3l
    imageId: eu-west-1/ami-f3f6d784
```
and then run:

```
nohup ./start.sh --app blueprints/seaclouds.yaml &
```

## Deploying SeaClouds on BYON

Make sure you have [Vagrant](https://www.vagrantup.com/), [Virtual Box](https://www.virtualbox.org/)

```bash
cd seaclouds-installer
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

# Building standalone SeaClouds distro

Make sure you have [Apache Maven](https://maven.apache.org/) v.3.5.5+

First of all you need to build the SeaCloudsPlatform project with:

```bash
mvn clean install -DskipTests
```

## Deploying SeaClouds on the cloud

```bash
cd usage/installer/target/seaclouds-installer-dist/seaclouds-installer
nohup ./start.sh --app blueprints/seaclouds.yaml --location <location> &
```

## Deploying SeaClouds on BYON

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

## SeaClouds release 0.7.0-M19

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
