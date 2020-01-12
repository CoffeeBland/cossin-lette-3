Array.prototype.remove && console.warn('Overriding existing function!');
Array.prototype.remove = function(e) {
    const i = this.indexOf(e);
    if (i === -1)
        return false;
    this.splice(i, 1);
    return true;
}
