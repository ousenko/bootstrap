# Android Project Template

### What

- Android project template, written in Kotlin.
- Structured in an MVP way.
- Contains project bootstrap boilerplate: gradle scripts, app versioning, signing, base classes, etc


### Features

- Auto-versioning of app artifacts based on Git history
- 99% of app logic is kept away from UI: using reactive two-way bindings in Presenters
- Uses single activity and multiple Conductor-controllers for scene management
- Introduces conventions over Controller/Presenter lifecycles (to prevent memory leaks and survive config changes)
- Utilizes Dagger 2 for Dependency Injection, on per-controller basis
- Retrofit 2 + OkHTTP for network stuff
- Contains hidden bugs you would love to search for! Just kidding (actually not)

### Why

- Just clone it, remove what you don't need, and add what is missing
