package com.example.robotcontroler.view;

public class ControlView<T> {
    private T view;

    public ControlView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }
}
