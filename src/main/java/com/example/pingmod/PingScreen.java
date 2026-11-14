package com.example.pingmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class PingScreen extends Screen {
    private static final int MAX = 10000;
    private ButtonWidget toggle;

    public PingScreen() {
        super(Text.literal("Ping Mod"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 30;

        this.addDrawableChild(new SliderWidget(cx - 100, y, 200, 20,
                Text.literal("Gecikme: " + PacketDelayer.delayMs + " ms"),
                PacketDelayer.delayMs / (double) MAX) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Gecikme: " + (int) (value * MAX) + " ms"));
            }

            @Override
            protected void applyValue() {
                PacketDelayer.delayMs = (int) (value * MAX);
            }
        });

        toggle = this.addDrawableChild(ButtonWidget.builder(label(), b -> {
            PacketDelayer.enabled = !PacketDelayer.enabled;
            b.setMessage(label());
        }).dimensions(cx - 100, y + 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("10000 ms yap"), b -> {
            PacketDelayer.delayMs = MAX;
            this.clearAndInit();
        }).dimensions(cx - 100, y + 55, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Kapat"), b -> this.close())
                .dimensions(cx - 100, y + 80, 200, 20).build());
    }

    private Text label() {
        return Text.literal("Ping artirma: " + (PacketDelayer.enabled ? "ACIK" : "KAPALI"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 55, 0xFFFFFF);
    }
}
