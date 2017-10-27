# MalmoAgent

In this work, we exploit the open-source framework [Malmo](#original-project) to build an Artificial Intelligence connected to the popular computer game [Minecraft](https://minecraft.net). We explore planning methods with hierarchical actions to control the main character and achieve predefined goals.

Please refer to the [project report](https://github.com/carlo-/MalmoAgent/blob/master/Report.pdf) for further details.

<p align="center">
  <img alt="Final Result" src="https://i.imgur.com/wjP93Yl.png" width="auto" height="600">
  <br/>
  Complex structure built by our agent
</p>

---
## Installation

1. [Download the pre-built version 0.30.0 of Malmo, for Windows, Linux or MacOSX.](https://github.com/Microsoft/malmo/releases/tag/0.30.0)

2. Navigate to the downloaded folder and clone this repository in it.

### Windows
3. Open a Powershell and run:
```PowerShell
Set-ExecutionPolicy -Scope CurrentUser Unrestricted
path:\Malmo-0.30.0-Windows-64bit\scripts
.\malmo_install.ps1
```

Refer to [this](https://msdn.microsoft.com/en-us/powershell/reference/5.1/microsoft.powershell.security/set-executionpolicy#example-4-set-the-scope-for-an-execution-policy) guide for details.

### macOS
3. Run the script `scripts/install_macosx.sh`

Refer to [this](https://github.com/Microsoft/malmo/blob/master/doc/install_macosx.md) guide for details.

### Linux
3. Follow the instructions [here](https://github.com/Microsoft/malmo/blob/master/doc/install_linux.md).

---
## Running the agent

In order to run the agent, first start a Minecraft instance and wait for it to load completely, then run the included MalmoAgent.jar with this command:

`java -cp MalmoAgent.jar;. main.JavaAgent`

If you wish to compile the project yourself, all of the sources files are contained in this repository, including the libraries.

---
## Original project
Johnson M., Hofmann K., Hutton T., Bignell D. (2016) [_The Malmo Platform for Artificial Intelligence Experimentation._](http://www.ijcai.org/Proceedings/16/Papers/643.pdf) [Proc. 25th International Joint Conference on Artificial Intelligence](http://www.ijcai.org/Proceedings/2016), Ed. Kambhampati S., p. 4246. AAAI Press, Palo Alto, California USA. https://github.com/Microsoft/malmo

---
## License
This work is released under the MIT license. See `LICENSE` for more information. Notice that some dependencies are bundled with this project, but are under terms of separate licenses.
