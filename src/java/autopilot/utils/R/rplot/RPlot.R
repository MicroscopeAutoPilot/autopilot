library(ggplot2)
       
d=data.frame(@ColumnList@)

pdf("@PlotFilePath@")

p <- ggplot(d, aes(@XAxisVariableName@)) 
@PlotYSeriesRCode@
p <- p + opts(plot.title=theme_text(size=@TitleFontSize@))
p <- p + opts(title = "@PlotTitle@")
p <- p + scale_x_continuous("@XAxisLabel@") 
p <- p + scale_y_continuous("@YAxisLabel@")
#p <- p + scale_colour_discrete(name="@LegendTitle@",
#                       labels=c(@LegendLabels@))
p <- p + scale_linetype_manual("", values=c(@LegendLineTypes@), breaks=c(@LegendLabels@))
                       
p <- p + opts(legend.position="@LegendPosition@")
p <- p + opts(legend.text=theme_text(size=@LegendTextFontSize@))
p <- p + opts(legend.title=theme_blank())
#text(size=@LegendTitleFontSize@)


p <- p + opts(axis.title.x=theme_text(size=@AxisLabelFontSize@))
p <- p + opts(axis.title.y=theme_text(size=@AxisLabelFontSize@, angle=90))


p

dev.off()
      
      
      
# old stuff:      
#p <- p + scale_colour_discrete(name = "@LegendTitle@")
# values=c(@LegendColors@), 
#p <- p + opts(legend.title="@LegendTitle@")
#                      breaks=c(@LegendBreaks@),
#geom_line(aes(y = V3, colour = "var1"))
#qplot(V1, V2, data = d, geom = "line", main="@PlotTitle@")