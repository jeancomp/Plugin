# Plugin (OpenDPMH) - Framework to Facilitate the Development of Digital Phenotyping Applications
> Plugin is an application that makes up the OpenDPMH framework, contains other data processing modules.


[![GitHub issues](https://img.shields.io/github/issues/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/issues)
[![GitHub forks](https://img.shields.io/github/forks/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/network)
[![GitHub stars](https://img.shields.io/github/stars/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1/stargazers)
[![GitHub license](https://img.shields.io/github/license/jeancomp/fenotipagem_digital_saude_vs_0_1)](https://github.com/jeancomp/fenotipagem_digital_saude_vs_0_1)
[![Twitter](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fjeancomp%2Ffenotipagem_digital_saude_vs_0_1)

Summary
=================
<!--ts-->
   * [Goals](#Goals)
   * [Project status](#Project-status)
   * [How to use](#How-to-use)
      * [Prerequisites](#Prerequisites)
      * [Installation](#Installation)
      * [Example of use](#Example-of-use)
   * [License](#License)
   * [Contribution](#Contribution)
   * [Author](#Author)
<!--te-->


Goals
=================
The Plugin aims to facilitate the development of new data processing modules, without the need to recompile the original project that is running the OpenDPMH Framework, nor to interrupt the user's raw data collection.

<h1 align="center">
  <img alt="Arquitetura-framework" title="#Arquitetura" src="/framework.png" />
</h1>

Core Components:
* PluginManager: class responsible for managing the plugin, uses the builder pattern to provide the settings for the plugin to work, such as informing the context, the list of available plugins, the client ID. When starting the plugin, the connection with the framework is made to exchange information (through CDDL), for example, the plugin sends the list of available data processing modules, then the framework returns the list of modules where is interested in starting, in the same way, to stop, receiving data from the sensors.
* DataProcessor: features the same settings and features available in the core.


![](header.png)


Project-status
=================

<h4 align="center"> 
	🚧  Framework - Under development...  🚧
</h4>

### Features

- [x] PluginManager
- [x] DataProcessor

How-to-use
==================

Prerequisites
-----
* Android version: 6
* Android API Version: minSdkVersion > 26

Installation
-----

Linux & Windows:

```sh
1º option (github project):
	* download the zip project, unzip it.
	* then open with in Android Studio "Open an Existing Project", ready.
```
```sh
2º option (aar files): under construction... 
```
```sh
3º option (apk): under construction... 
```

Example-of-use
-----
PluginManager
```sh
public PluginManager pluginManager;
```
Start-plugin:
```sh
```
```sh
pluginManager = new PluginManager.Builder(this)
                .setProcessorList(processorList)
                .setClientID("jeancomp")
                .build();
        pluginManager.start();
```
Stop-plugin:
```sh
pluginManager.stop();
```
Start-activeDataprocessor:
```sh
List<String> processorList = {nameProcessor1,nameProcessor2,...};
pluginManager.getInstance().startDataProcessors(listProcessors);
```
Stop-sensor:
```sh
List<String> processorList = {nameProcessor1,nameProcessor2,...};
pluginManager.getInstance().stopDataProcessors(listProcessors);
```

License
=================

Your Name – [@Twitter](https://twitter.com/jeancomp) – jean.marques@lsdi.ufma.br

Distributed under the XYZ license. See ``LICENSE`` for more information.

[https://github.com/](https://github.com/jeancomp)

Contribution
=================

Main developer:
1.  Jean Pablo (<https://github.com/jeancomp>)


Contributors:
1. Ariel Teles (https://github.com/arielsteles)
2. André

<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
[wiki]: https://github.com/yourname/yourproject/wiki


Author
=================

<a href="https://github.com/jeancomp">
       <a href="https://imgbb.com/"><img src="https://i.ibb.co/MsLwGfj/jp.jpg" alt="jp" border="0" width="80px;" /></a>
 <br />
 <sub><b>Jean Pablo</b></sub></a>


Made by Jean Pablo 👋🏽 Contact!

[![Twitter](https://img.shields.io/twitter/url?label=%40jeancomp&style=social&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Ftwitter.com%2Fjeancomp)
[![Linkedin Badge](https://img.shields.io/badge/-Jean-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jean-pablo-marques-mendes/)](https://www.linkedin.com/in/jean-pablo-marques-mendes/) 
[![Gmail Badge](https://img.shields.io/badge/-jeancomp@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:jeancomp@gmail.com)](mailto:jeancomp@gmail.com)

