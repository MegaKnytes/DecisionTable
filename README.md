# DecisionTable

## XML Schema

### `SystemConfiguration`

```xml
<SystemConfiguration name="CompetitionBot" enabled="true">
    <States>
        <State name="ClawState">
            <Value name="OPEN"/>
            <Value name="CLOSED"/>
        </State>
    </States>

    <Devices>
        <Device driver="Servo" name="ClawServo">
            <Parameter name="HardwareMap" value="claw_servo">
                <Parameter name="Direction" value="FORWARD"/>
            </Parameter>
        </Device>

        <Device driver="Gamepad" name="DriverOne">
            <Parameter name="ID" value="1"/>
        </Device>
    </Devices>

    <InternalVariables>
        <VariableGroup name="Claw">
            <Variable name="State" type="ClawState" value="OPEN" description="Current claw position"/>
            <Variable name="OpenPosition" type="Double" value="0.8"/>
            <Variable name="ClosedPosition" type="Double" value="0.2"/>
        </VariableGroup>
    </InternalVariables>
</SystemConfiguration>
```

- **`<Devices>`**: one `<Device>` per physical hardware component that exists in the hardware map, with the name of a driver (`Servo`, `Motor`, `Gamepad`, `MecanumDrive`..., and its parameters.
- **`<States>`**: Exist for expressing state machines.
- **`<InternalVariables>`**: Values that live outside of a device. Types are `Boolean`, `Double`, `Float`, `Integer`, `String`, or the name of a declared `<State>`.

### `DecisionTable`

```xml
<DecisionTable name="TeleOp" enabled="true" type="TELEOP" systemConfiguration="CompetitionBot" transitionTarget="">
    <Rules>
        <RuleGroup name="Claw">
            <Rule name="Open claw on A press" priority="1">
                <Condition>
                    <And>
                        <Equals target="Driver.Button.A" value="true"/>
                        <NotEquals target="prev:Driver.Button.A" value="true"/>
                        <Equals target="var:Claw.State" value="CLOSED"/>
                    </And>
                </Condition>
                <Action>
                    <Set target="var:Claw.State" value="OPEN"/>
                </Action>
            </Rule>

            <Rule name="Apply claw position from state" priority="2">
                <Condition>
                    <Equals target="var:Claw.State" value="OPEN"/>
                </Condition>
                <Action>
                    <Set target="ClawServo.Position" ref="var:Claw.OpenPosition"/>
                </Action>
            </Rule>
        </RuleGroup>
    </Rules>
</DecisionTable>
```

- **`type`** is `TELEOP`, `AUTONOMOUS`, or `UTILITY`
- **`<RuleGroup>`**: Organize all related rules together (ex. by subsystem). A group can be disabled with `enabled="false"` to disable it at load.
- **`<Condition>`**: a boolean expression: `Equals`, `NotEquals`, `GreaterThan`, `LessThan`, `GreaterOrEqual`, `LessOrEqual`, `And`, `Or`, `XOr`, `Not`, `Always`, and `Once`.
- **`<Action>`** is one or more `<Set>` writes, each taking a `value="..."` or a `ref="..."` to another
  address.

## Addressing values

| Form                     | Meaning                                               |
|--------------------------|-------------------------------------------------------|
| `param:Device.Parameter` | A device's parameter (readable and writable).         |
| `prev:Device.Parameter`  | A parameter's value as of the last loop. (Read only). |
| `var:Group.Name`         | An internal variable (Readable and writable).         |

## Built-in drivers

The following drivers are provided by the `FTC` module (SUBJECT TO CHANGE):

- **Raw hardware**: `Servo`, `CRServo`, `Motor`, `Gamepad`, `IMU`, `ColorSensor`, `DistanceSensor`, `TouchSensor`,
  `DigitalChannel`, `AnalogInput`, `VoltageSensor`
- **Common code**: `MecanumDrive`, `TankDrive`, `PIDF`, `Timer`, `Clock`, `GoBildaHeadlight`

A driver is any class that implements `Device` (through `HardwareMapDevice` or `OpModeDevice` for hardware access) that registers its parameters through the `ParameterRegistry`. The same thing applies to custom `ValueParser` implementations for new types.