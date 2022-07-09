for (const i of selector("@e")) {
    if (i.get_pos().y <= 50) {
        let pos = i.get_pos();
        pos.y = 255;
        i.set_pos(pos);
    }
}