#!/bin/bash

consul_kv_prefix="config"

for file in *.yaml; do
    name="${file%.yaml}"
    consul kv put "$consul_kv_prefix/$name/data" - < "$file"
done
