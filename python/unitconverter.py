import knime.extension as knext


def is_numeric(column: knext.Column) -> bool:
    """
    Checks if column is numeric e.g. int, long or double.
    @return: True if Column is numeric
    """
    return (
        column.ktype == knext.double()
        or column.ktype == knext.int32()
        or column.ktype == knext.int64()
    )


# ────────────────────────────────────────────────────────────────
# 1. Parameter group: one conversion “row” inside the array
# ────────────────────────────────────────────────────────────────
@knext.parameter_group("Conversion Settings")
class _Conversion:
    input_column = knext.ColumnParameter(
        label="Input column",
        description="Numeric column to convert",
        column_filter=is_numeric,
    )

    class _Which(knext.EnumParameterOptions):
        C_TO_F = ("°C → °F (* 9/5 + 32)", "Celsius to Fahrenheit")
        F_TO_C = ("°F → °C (- 32) * 5/9", "Fahrenheit to Celsius")
        KM_TO_MI = ("km → mi (/ 1.60934)", "Kilometres to miles")
        MI_TO_KM = ("mi → km (* 1.60934)", "Miles to kilometres")
        L_TO_GAL = ("L → gal (/ 3.78541)", "Litres to US gallons")
        GAL_TO_L = ("gal → L (* 3.78541)", "US gallons to litres")

    conversion = knext.EnumParameter(
        label="Conversion",
        description="Unit conversion to apply",
        default_value=_Which.C_TO_F.name,
        enum=_Which,
    )

    output_column = knext.StringParameter(
        label="Output column name",
        description="Name of the new column",
        default_value="converted",
    )

    as_string = knext.BoolParameter(
        label="Return as string with unit suffix",
        description="If unchecked, result is numeric",
        default_value=False,
    )


# ────────────────────────────────────────────────────────────────
# 2. The node class itself
# ────────────────────────────────────────────────────────────────
@knext.node(
    name="Unit Converter (Python)",
    node_type=knext.NodeType.MANIPULATOR,
    icon_path="node-cog.png",
    category="/labs/",
)
@knext.input_table(
    name="Input data (must contain numeric column[s])",
    description="Table that holds the values to convert",
)
@knext.output_table(
    name="Converted data",
    description="Original table plus converted column(s)",
)
class UnitConverterNode:
    """Convert between common metric / imperial units.

    Extend the ParameterArray to support further conversions
    by simply adding new items in the dialog.
    """

    conversions = knext.ParameterArray(
        label="Conversions",
        description="Add one or more conversions",
        parameters=_Conversion(),
        layout_direction=knext.LayoutDirection.VERTICAL,
        array_title="Unit conversion",
        button_text="Add unit conversion",
    )

    # ------ configure() builds the output spec so columns show up immediately
    def configure(self, cfg_ctx, input_spec):
        output_spec = input_spec

        for conv in self.conversions:
            new_name = conv.output_column
            if new_name in output_spec.column_names:
                cfg_ctx.set_warning(
                    f"Column '{new_name}' already exists and will be overwritten."
                )
                output_spec = output_spec.remove(new_name)

            if conv.as_string:
                output_spec = output_spec.append(knext.Column(knext.string(), new_name))
            else:
                output_spec = output_spec.append(knext.Column(knext.double(), new_name))

        return output_spec

    # ------ execute() does the real work
    def execute(self, exec_ctx, input_table):
        df = input_table.to_pandas()
        for conv in self.conversions:
            col = conv.input_column
            out = conv.output_column

            series = df[col].astype(float)  # safe cast

            match _Conversion._Which[conv.conversion]:
                case _Conversion._Which.C_TO_F:
                    series = series * 9 / 5 + 32
                    unit = " °F"
                case _Conversion._Which.F_TO_C:
                    series = (series - 32) * 5 / 9
                    unit = " °C"
                case _Conversion._Which.KM_TO_MI:
                    series = series / 1.60934
                    unit = " mi"
                case _Conversion._Which.MI_TO_KM:
                    series = series * 1.60934
                    unit = " km"
                case _Conversion._Which.L_TO_GAL:
                    series = series / 3.78541
                    unit = " gal"
                case _Conversion._Which.GAL_TO_L:
                    series = series * 3.78541
                    unit = " L"
                case _:
                    raise ValueError(f"Unknown conversion selected: {conv.conversion.name}")

            df[out] = series.round(6).astype(str) + unit if conv.as_string else series

        return knext.Table.from_pandas(df)
