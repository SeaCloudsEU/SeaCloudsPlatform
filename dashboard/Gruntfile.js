/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

module.exports = function (grunt) {
    var srcDirectory = 'src/main/resources/webapp/';

    grunt.initConfig({
        wiredep: {
            app: {
                src: [
                    srcDirectory + 'index.html'
                ],
                ignorePath: srcDirectory
            }
        },

        copy: {
            dist: {
                files: [{
                    //for bootstrap fonts
                    expand: true,
                    dot: true,
                    cwd: 'bower_components/bootstrap/dist',
                    src: ['fonts/*.*'],
                    dest: srcDirectory + 'static/'
                }, {
                    //for font-awesome
                    expand: true,
                    dot: true,
                    cwd: 'bower_components/components-font-awesome',
                    src: ['fonts/*.*'],
                    dest: srcDirectory + 'static/'
                }]
            },
        },

        useminPrepare: {
            html: [srcDirectory + 'index.html'],
            options: {
                dest: srcDirectory
            }
        },

        usemin: {
            html: srcDirectory + 'index.html'
        }

    });

    grunt.loadNpmTasks('grunt-wiredep');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-usemin');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.registerTask('default', ['wiredep', 'copy', 'useminPrepare', 'concat', 'uglify', 'cssmin', 'usemin']);
};