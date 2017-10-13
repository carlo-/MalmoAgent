# MalmoAgent

This group work is based on Microsoft's Project Malmo: an Artificial Intelligence platform for AI training and research. In order to run it, one needs to unpack project Malmo and also some dependent binaries. It might seem somewhat complex at first hand but I can assure that that is not the case and if done correctly, it should be up and running in no time!

An updated version of this README file can be found at [this link](https://github.com/carlo-/MalmoAgent).

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
3. Run the script `install_macosx.sh`

Refer to [this](https://github.com/Microsoft/malmo/blob/master/doc/install_macosx.md) guide for details.

### Linux
3. Follow the instructions [here](https://github.com/Microsoft/malmo/blob/master/doc/install_linux.md).

---
## Running the agent

To run Malmo please follow [these](https://github.com/Microsoft/malmo) instructions.

---
## Original project
Johnson M., Hofmann K., Hutton T., Bignell D. (2016) [_The Malmo Platform for Artificial Intelligence Experimentation._](http://www.ijcai.org/Proceedings/16/Papers/643.pdf) [Proc. 25th International Joint Conference on Artificial Intelligence](http://www.ijcai.org/Proceedings/2016), Ed. Kambhampati S., p. 4246. AAAI Press, Palo Alto, California USA. https://github.com/Microsoft/malmo

---
## License
This work is released under the MIT license. See `LICENSE` for more information.
